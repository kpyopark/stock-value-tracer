package streamProcess.krx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import post.KrxItem;
import streamProcess.StreamWatcher;
import dao.KrxItemDao;

public class KrxStreamInserter {

	ArrayBlockingQueue<KrxItem> checkExistAlreadyQueue = null;
	ArrayBlockingQueue<KrxItem> insertQueue = null;
	static int MAX_CHECKER = 40;
	static int MAX_INSERTER = 80;
	static int MAX_QUEUE_SiZE = 4000;
	boolean needExit = false;
	
	ExecutorService checkService = null;
	ExecutorService insertService = null;
	
	public KrxStreamInserter() {
		checkExistAlreadyQueue = new ArrayBlockingQueue<KrxItem>(MAX_QUEUE_SiZE);
		insertQueue = new ArrayBlockingQueue<KrxItem>(MAX_QUEUE_SiZE);
	}
	
	public KrxStreamInserter(ArrayBlockingQueue<KrxItem> checkQueue) {
		this.checkExistAlreadyQueue = checkQueue;
		insertQueue = new ArrayBlockingQueue<KrxItem>(MAX_QUEUE_SiZE);
	}
	
	public KrxStreamInserter(ArrayBlockingQueue<KrxItem> checkQueue,ArrayBlockingQueue<KrxItem> inserterQueue) {
		this.checkExistAlreadyQueue = checkQueue;
		this.insertQueue = inserterQueue;
	}
	
	public ArrayBlockingQueue<KrxItem> getSourceQueue() {
		return this.checkExistAlreadyQueue;
	}
	
	public ArrayBlockingQueue<KrxItem> getDestinationQueue() {
		return this.insertQueue;
	}
	
	public void addKrxItem(KrxItem item) throws InterruptedException {
		checkExistAlreadyQueue.put(item);
	}
	
	class CheckExistAlready implements Runnable {
		KrxItemDao dao = null;
		BlockingQueue<KrxItem> source = null;
		BlockingQueue<KrxItem> dst = null;
		public CheckExistAlready(BlockingQueue<KrxItem> source_, BlockingQueue<KrxItem> dst_) {
			source = source_;
			dst = dst_;
			dao = new KrxItemDao();
		}
		public void run() {
			while(!needExit) {
				try {
					KrxItem newItem = source.poll(1, TimeUnit.SECONDS);
					if ( newItem != null ) {
						//System.out.println("check :" + newItem.getId() + ":" + newItem.getStandardDate() );
						//if ( dao.select(newItem, newItem.getStandardDate()) == null ) {
							dst.put(newItem);
						//}
					}
				} catch ( Exception e ) { e.printStackTrace();}
			}
		}
	}
	
	class KrxItemInserter implements Runnable {
		KrxItemDao dao = null;
		BlockingQueue<KrxItem> source = null;
		public KrxItemInserter(BlockingQueue<KrxItem> source_) {
			source = source_;
			dao = new KrxItemDao();
		}
		public void run() {
			while(!needExit) {
				try {
					KrxItem newItem = source.poll(1, TimeUnit.SECONDS);
					if ( newItem != null ) {
						//System.out.println("insert :" + newItem.getId() + ":" + newItem.getStandardDate() );
						dao.insert(newItem);
					}
				} catch ( Exception e ) { e.printStackTrace();}
			}
		}
	}
	
	public void startStream() {
		needExit = false;
		checkService = Executors.newFixedThreadPool(MAX_CHECKER);
		for ( int cnt = 0; cnt < MAX_CHECKER ; cnt++ ) {
			checkService.execute(new CheckExistAlready(checkExistAlreadyQueue, insertQueue));
		}
		insertService = Executors.newFixedThreadPool(MAX_INSERTER);
		for ( int cnt = 0; cnt < MAX_INSERTER ; cnt++ ) {
			insertService.execute(new KrxItemInserter(insertQueue));
		}
		StreamWatcher watcher = StreamWatcher.getStreamWatcher();
		watcher.addWatchTarget("checkQueue", checkExistAlreadyQueue);
		watcher.addWatchTarget("insertQueue", insertQueue);
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
		watcher.removeWatchTarget("checkQueue");
		watcher.removeWatchTarget("insertQueue");
	}
	
}