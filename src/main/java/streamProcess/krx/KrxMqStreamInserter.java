package streamProcess.krx;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.esotericsoftware.kryo.Kryo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;

import common.QueueUtil;
import dao.KrxItemDao;
import post.KrxItem;
import streamProcess.StreamWatcher;

public class KrxMqStreamInserter {

	boolean needExit = false;
	static int MAX_CHECKER = 10;
	static int MAX_INSERTER = 20;
	static int MAX_QUEUE_SiZE = 4000;
	
	ExecutorService checkService = null;
	ExecutorService insertService = null;
	
	public KrxMqStreamInserter() throws IOException {
	}
	
	class CheckExistAlready implements Runnable {
		KrxItemDao dao = null;
		Connection mqCon = null;
		
		Channel selectQueueChannel = null;
		Channel insertQueueChannel = null;

		public CheckExistAlready() throws IOException, TimeoutException {
			dao = new KrxItemDao();
			createQueues();
		}

		public void createQueues() throws IOException, TimeoutException {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			mqCon = factory.newConnection();
			selectQueueChannel = mqCon.createChannel();
			insertQueueChannel = mqCon.createChannel();
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-length", QueueUtil.QUEUE_SELECTKRX_MAX_DEPTH);
			selectQueueChannel.queueDeclare(QueueUtil.QUEUE_SELECTKRX, false, false, false, args);
			args.put("x-max-length", QueueUtil.QUEUE_INSERTKRX_MAX_DEPTH);
			insertQueueChannel.queueDeclare(QueueUtil.QUEUE_INSERTKRX, false, false, false, args);
			insertQueueChannel.queuePurge(QueueUtil.QUEUE_INSERTKRX);
		}
		
		public void closeQueue() {
			try { selectQueueChannel.close(); } catch (Exception ioe) {}
			try { insertQueueChannel.close(); } catch (Exception ioe) {}
			try { mqCon.close(); } catch (IOException ioe) {}
		}

		public void run() {
			Kryo serializer = new Kryo();
			serializer.register(KrxItem.class);
			DefaultConsumer consumer = null;
			try {
				consumer = new DefaultConsumer(selectQueueChannel);
				selectQueueChannel.basicConsume(QueueUtil.QUEUE_SELECTKRX, true, consumer);
			} catch ( IOException ioe ) {
				ioe.printStackTrace();
				needExit = true;
			}
			while(!needExit) {
				try {
					/*
					Delivery delivery = consumer.nextDelivery();
					KrxItem newItem = QueueUtil.getInstanceFromBytes(serializer, delivery.getBody(), KrxItem.class);
					if ( newItem != null ) {
						//System.out.println("check :" + newItem.getId() + ":" + newItem.getStandardDate() );
						if ( dao.select(newItem, newItem.getStandardDate()) == null ) {
							insertQueueChannel.basicPublish("", QueueUtil.QUEUE_INSERTKRX, null, delivery.getBody());
						}
					}
					*/
				} catch ( Exception e ) { e.printStackTrace();}
			}
			closeQueue();
		}
	}
	
	class KrxItemInserter implements Runnable {
		KrxItemDao dao = null;
		Connection mqCon = null;
		Channel insertQueueChannel = null;
		/* static */ final int MAX_BULK_SIZE = 100;
		public KrxItemInserter() throws IOException, TimeoutException {
			dao = new KrxItemDao();
			createQueues();
		}
		public void createQueues() throws IOException, TimeoutException {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			mqCon = factory.newConnection();
			insertQueueChannel = mqCon.createChannel();
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-length", QueueUtil.QUEUE_INSERTKRX_MAX_DEPTH);
			insertQueueChannel.queueDeclare(QueueUtil.QUEUE_INSERTKRX, false, false, false, args);
		}
		public void closeQueue() {
			try { insertQueueChannel.close(); } catch (Exception ioe) {}
			try { mqCon.close(); } catch (IOException ioe) {}
		}

		private void tryToBulkInsert(List<KrxItem> krxItems) {
			try {
				dao.insert(krxItems);
			} catch ( SQLException sqle ) {
				List<KrxItem> oneItemList = new ArrayList<KrxItem>();
				for(KrxItem krxItem:krxItems) {
					try {
						oneItemList.clear();
						oneItemList.add(krxItem);
						dao.insert(oneItemList);
					} catch (SQLException sqle2) {
						sqle2.printStackTrace();
					}
				}
			} finally {
				krxItems.clear();
			}
		}

		public void run() {
			Kryo serializer = new Kryo();
			serializer.register(KrxItem.class);
			DefaultConsumer consumer = null;
			try {
				consumer = new DefaultConsumer(insertQueueChannel);
				insertQueueChannel.basicConsume(QueueUtil.QUEUE_INSERTKRX, true, consumer);
			} catch ( IOException ioe ) {
				ioe.printStackTrace();
				needExit = true;
			}
			List<KrxItem> bulk = new ArrayList<KrxItem>();
			int size = 0;
			while(!needExit) {
				try {
					/*
					QueueingConsumer.Delivery delivery = consumer.nextDelivery(1000);
					if ( delivery != null && delivery.getBody() != null ) {
						KrxItem newItem = QueueUtil.getInstanceFromBytes(serializer, delivery.getBody(), KrxItem.class);
						bulk.add(newItem);
						size++;
						if ( size > MAX_BULK_SIZE ) {
							tryToBulkInsert(bulk);
							size = 0;
						}
					} else {
						tryToBulkInsert(bulk);
						size = 0;
					}
					*/
				} catch ( Exception e ) { e.printStackTrace();}
			}
			closeQueue();
		}
	}
	
	public void startStream() {
		needExit = false;
		checkService = Executors.newFixedThreadPool(MAX_CHECKER);
		try {
			for ( int cnt = 0; cnt < MAX_CHECKER ; cnt++ ) {
				checkService.execute(new CheckExistAlready());
			}
			insertService = Executors.newFixedThreadPool(MAX_INSERTER);
			for ( int cnt = 0; cnt < MAX_INSERTER ; cnt++ ) {
				insertService.execute(new KrxItemInserter());
			}
			StreamWatcher watcher = StreamWatcher.getStreamWatcher();
			watcher.addWatchTarget(QueueUtil.QUEUE_INSERTKRX);
		} catch (Exception ioe) {
			ioe.printStackTrace();
			// When the exceptions occurs during initialization, all resource will be canceled.
			stopStream();
		}
	}
	
	public void stopStream() {
		needExit = true;
		try {
			checkService.shutdown();
		} catch ( Exception e ) {}
		try {
			insertService.shutdown();
		} catch ( Exception e ) {}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.removeWatchTarget(QueueUtil.QUEUE_INSERTKRX);
	}
	
}
