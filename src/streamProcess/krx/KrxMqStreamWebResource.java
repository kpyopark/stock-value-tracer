package streamProcess.krx;

import internetResource.companyItem.CompanyAndItemListResource2016FromKrx;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import post.KrxItem;
import post.KrxSecurityType;
import streamProcess.StreamWatcher;

import com.esotericsoftware.kryo.Kryo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import common.QueueUtil;

public class KrxMqStreamWebResource {

	class KrxWebResourceTask implements Serializable {
		private static final long serialVersionUID = 1L;
		String standarddate;
		KrxSecurityType type;
		public KrxWebResourceTask(String standardDate, KrxSecurityType type) {
			KrxWebResourceTask.this.standarddate = standardDate;
			KrxWebResourceTask.this.type = type;
		}
	}
	
	ArrayBlockingQueue<KrxWebResourceTask> webResourceTask = null;
	
	static int MAX_WEBRESOURCE = 10;
	static int MAX_QUEUE_SIZE = 15;
	boolean needExit = false;
	
	ExecutorService webResourceService = null;
	
	public KrxMqStreamWebResource() throws IOException {
		webResourceTask = new ArrayBlockingQueue<KrxWebResourceTask>(MAX_QUEUE_SIZE);
	}

	public void addWebResourceTask(String standardDate, KrxSecurityType type) throws InterruptedException {
		webResourceTask.put(new KrxWebResourceTask(standardDate, type));
	}
	
	class KrxItemCrawlerService implements Runnable {
		BlockingQueue<KrxWebResourceTask> source = null;
		Connection mqCon = null;
		Channel selectQueueChannel = null;
		CompanyAndItemListResource2016FromKrx ir = new CompanyAndItemListResource2016FromKrx();		
		public KrxItemCrawlerService(BlockingQueue<KrxWebResourceTask> source_) throws IOException {
			source = source_;
			createDestinationQueue();
		}

		public void createDestinationQueue() throws IOException {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			mqCon = factory.newConnection();
			selectQueueChannel = mqCon.createChannel();
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("x-max-length", QueueUtil.QUEUE_SELECTKRX_MAX_DEPTH);
			//selectQueueChannel.queueDelete(QueueUtil.QUEUE_SELECTKRX);
			selectQueueChannel.queueDeclare(QueueUtil.QUEUE_SELECTKRX, false, false, false, args);
			selectQueueChannel.queuePurge(QueueUtil.QUEUE_SELECTKRX);
		}
		public void closeQueue() throws IOException {
			try {
				if ( selectQueueChannel != null ) 
					selectQueueChannel.close();
			} finally {
				if ( mqCon != null )
					mqCon.close();
			}
		}
		
		public void run() {
			byte[] bufferForSerialization = new byte[4096];
			Kryo serializer = new Kryo();
			serializer.register(KrxItem.class);
			while(!needExit) {
				try {
					KrxWebResourceTask newItem = source.poll(1, TimeUnit.SECONDS);
					if ( newItem != null ) {
						//System.out.println("Web Resource :" + newItem.standarddate + ":" + newItem.type.getType());
						ArrayList<KrxItem> krxItemList = ir.getItemList(newItem.type, newItem.standarddate, null);
						for ( int cnt = 0 ; cnt < krxItemList.size(); cnt++ ) {
							selectQueueChannel.basicPublish("", QueueUtil.QUEUE_SELECTKRX, null, QueueUtil.getBytesFromObject(serializer, bufferForSerialization, 4096, krxItemList.get(cnt)));
						}
					}
				} catch ( Exception e ) { e.printStackTrace(); }
			}
			try {
				closeQueue();
			} catch ( Exception e ) { e.printStackTrace(); }
		}
		
	}

	public void startStream() {
		needExit = false;
		try {
			webResourceService = Executors.newFixedThreadPool(MAX_WEBRESOURCE);
			for ( int cnt = 0; cnt < MAX_WEBRESOURCE ; cnt++ ) {
				webResourceService.execute(new KrxItemCrawlerService(webResourceTask));
			}
			StreamWatcher watcher = StreamWatcher.getStreamWatcher();
			watcher.addWatchTarget(QueueUtil.QUEUE_WEBRESOURCE, webResourceTask);
			watcher.addWatchTarget(QueueUtil.QUEUE_SELECTKRX);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			needExit = true;
			stopStream();
		}
	}
	
	public void stopStream() {
		needExit = true;
		try {
			webResourceService.shutdown();
			webResourceService.awaitTermination(2000, TimeUnit.MILLISECONDS);
		} catch ( Exception e ) {}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.removeWatchTarget(QueueUtil.QUEUE_WEBRESOURCE);
		watcher.removeWatchTarget(QueueUtil.QUEUE_SELECTKRX);
	}

}
