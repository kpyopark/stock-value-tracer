package streamProcess;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class StreamWatcher implements Runnable {
	
	HashMap<String, BlockingQueue> watchTargets = new HashMap<String,BlockingQueue>();

	boolean needExit = false;
	static StreamWatcher singletone = null;
	Thread currentThread = null;
	
	private StreamWatcher() {
	}
	
	public static StreamWatcher getStreamWatcher() {
		if ( singletone == null ) {
			singletone = new StreamWatcher();
		}
		return singletone;
	}
	
	public void addWatchTarget(String name,BlockingQueue target) {
		watchTargets.put(name, target);
		if ( currentThread == null )
			start();
	}
	
	public void removeWatchTarget(String name) {
		watchTargets.remove(name);
		if ( watchTargets.isEmpty() )
			stop();
	}
	
	public void run() {
		try {
			while(!needExit) {
				for( String name : watchTargets.keySet() ) {
					System.out.println("name[" + name + "]:" + watchTargets.get(name).size() );
				}
				Thread.sleep(2000);
			}
		} catch ( Exception e ) {}
	}
	
	private void start() {
		if ( currentThread == null ) {
			needExit = false;
			currentThread = new Thread(singletone);
			currentThread.start();
		}
	}
	
	private void stop() {
		if ( currentThread != null ) {
			needExit = true;
			currentThread.interrupt();
		}
		currentThread = null;
	}

}
