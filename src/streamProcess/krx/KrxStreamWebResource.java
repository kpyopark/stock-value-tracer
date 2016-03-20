package streamProcess.krx;

import internetResource.companyItem.CompanyAndItemListResource2016FromKrx;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import post.KrxItem;
import post.KrxSecurityType;
import streamProcess.StreamWatcher;

public class KrxStreamWebResource {

	class KrxWebResourceTask {
		String standarddate;
		KrxSecurityType type;
		public KrxWebResourceTask(String standardDate, KrxSecurityType type) {
			KrxWebResourceTask.this.standarddate = standardDate;
			KrxWebResourceTask.this.type = type;
		}
	}
	
	ArrayBlockingQueue<KrxWebResourceTask> webResourceTask = null;
	ArrayBlockingQueue<KrxItem> krxItemQueue = null;
	
	static int MAX_WEBRESOURCE = 30;
	static int MAX_QUEUE_SIZE = 30;
	boolean needExit = false;
	
	ExecutorService webResourceService = null;
	
	public KrxStreamWebResource() {
		webResourceTask = new ArrayBlockingQueue<KrxWebResourceTask>(MAX_QUEUE_SIZE);
		krxItemQueue = new ArrayBlockingQueue<KrxItem>(4000);
	}

	public ArrayBlockingQueue<KrxItem> getDestinationQueue() {
		return krxItemQueue;
	}
	
	public void addWebResourceTask(String standardDate, KrxSecurityType type) throws InterruptedException {
		webResourceTask.put(new KrxWebResourceTask(standardDate, type));
	}
	
	class KrxItemCrawlerService implements Runnable {
		BlockingQueue<KrxWebResourceTask> source = null;
		BlockingQueue<KrxItem> dst = null;
		CompanyAndItemListResource2016FromKrx ir = new CompanyAndItemListResource2016FromKrx();		
		public KrxItemCrawlerService(BlockingQueue<KrxWebResourceTask> source_, BlockingQueue<KrxItem> dst_) {
			source = source_;
			dst = dst_;
		}
		
		public void run() {
			while(!needExit) {
				try {
					KrxWebResourceTask newItem = source.poll(1, TimeUnit.SECONDS);
					if ( newItem != null ) {
						//System.out.println("Web Resource :" + newItem.standarddate + ":" + newItem.type.getType());
						ArrayList<KrxItem> krxItemList = ir.getItemList(newItem.type, newItem.standarddate, null);
						for ( int cnt = 0 ; cnt < krxItemList.size(); cnt++ ) {
							dst.put(krxItemList.get(cnt));
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
			webResourceService.execute(new KrxItemCrawlerService(webResourceTask, krxItemQueue));
		}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.addWatchTarget("webResource", webResourceTask);
	}
	
	public void stopStream() {
		needExit = true;
		try {
			webResourceService.shutdown();
		} catch ( Exception e ) {}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.removeWatchTarget("webResource");
	}

}
