package streamProcess.krx;

import internetResource.companyItem.CompanyAndItemListResourceFromKrx;

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

import com.esotericsoftware.kryo.Kryo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import common.QueueUtil;
import post.KrxItem;
import post.KrxSecurityType;
import streamProcess.StreamWatcher;

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
	Connection mqCon = null;
	Channel selectQueueChannel = null;
	
	static int MAX_WEBRESOURCE = 30;
	static int MAX_QUEUE_SIZE = 30;
	boolean needExit = false;
	
	ExecutorService webResourceService = null;
	
	public KrxMqStreamWebResource() throws IOException {
		webResourceTask = new ArrayBlockingQueue<KrxWebResourceTask>(MAX_QUEUE_SIZE);
		createDestinationQueue();
	}

	public void createDestinationQueue() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		mqCon = factory.newConnection();
		selectQueueChannel = mqCon.createChannel();
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-max-length", 5000);
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
	
	public void addWebResourceTask(String standardDate, KrxSecurityType type) throws InterruptedException {
		webResourceTask.put(new KrxWebResourceTask(standardDate, type));
	}
	
	class KrxItemCrawlerService implements Runnable {
		BlockingQueue<KrxWebResourceTask> source = null;
		Channel dst = null;
		CompanyAndItemListResourceFromKrx ir = new CompanyAndItemListResourceFromKrx();		
		public KrxItemCrawlerService(BlockingQueue<KrxWebResourceTask> source_, Channel dst_) {
			source = source_;
			dst = dst_;
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
							dst.basicPublish("", QueueUtil.QUEUE_SELECTKRX, null, QueueUtil.getBytesFromObject(serializer, bufferForSerialization, 4096, krxItemList.get(cnt)));
						}
					}
				} catch ( Exception e ) {}
			}
		}
		
	}

	public void startStream() {
		needExit = false;
		webResourceService = Executors.newFixedThreadPool(MAX_WEBRESOURCE);
		for ( int cnt = 0; cnt < MAX_WEBRESOURCE ; cnt++ ) {
			webResourceService.execute(new KrxItemCrawlerService(webResourceTask, selectQueueChannel));
		}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.addWatchTarget("webResource", webResourceTask);
		watcher.addWatchTarget(QueueUtil.QUEUE_SELECTKRX);
	}
	
	public void stopStream() {
		needExit = true;
		try {
			webResourceService.shutdown();
			webResourceService.awaitTermination(2000, TimeUnit.MILLISECONDS);
		} catch ( Exception e ) {}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.removeWatchTarget("webResource");
		watcher.removeWatchTarget(QueueUtil.QUEUE_SELECTKRX);
	}

}
