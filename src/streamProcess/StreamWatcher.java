package streamProcess;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import common.QueueUtil;

public class StreamWatcher implements Runnable {
	
	HashMap<String, BlockingQueue> watchTargets = new HashMap<String,BlockingQueue>();
	HashMap<String, ConnectionAndChannel> watchChannels = new HashMap<String, ConnectionAndChannel>();
	final static int MAX_WAITING_TIME = 2000;

	boolean needExit = false;
	static StreamWatcher singletone = null;
	Thread currentThread = null;
	
	private StreamWatcher() {
	}
	
	class ConnectionAndChannel {
		public Connection con;
		public Channel channel;
	}
	
	public static StreamWatcher getStreamWatcher() {
		if ( singletone == null ) {
			singletone = new StreamWatcher();
		}
		return singletone;
	}
	
	private ConnectionAndChannel createChannel(String queueName) throws IOException {
		ConnectionAndChannel connPair = new ConnectionAndChannel();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connPair.con = factory.newConnection();
		connPair.channel = connPair.con.createChannel();
		return connPair;
	}
	
	public void addWatchTarget(String name,BlockingQueue target) {
		watchTargets.put(name, target);
		if ( currentThread == null )
			start();
	}

	public void addWatchTarget(String name) {
		try {
			watchChannels.put(name, createChannel(name));
			if ( currentThread == null )
				start();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// Skip.
		}
	}
	
	public void removeWatchTarget(String name) {
		watchTargets.remove(name);
		if (watchChannels.containsKey(name)) {
			ConnectionAndChannel cac = watchChannels.get(name);
			try {
				cac.channel.close();
			} catch (IOException ioe) {
				// skip.
			} finally {
				try { cac.con.close(); } catch (IOException ioe) {}
			}
			watchChannels.remove(name);
		}
		if ( watchTargets.isEmpty() && watchChannels.isEmpty() )
			stop();
	}
	
	public int getQueueSize(String name) throws IOException {
		Channel channel = watchChannels.get(name).channel;
		return channel.queueDeclarePassive(name).getMessageCount();
	}
	
	public void run() {
		while(!needExit) {
			try {
				for( String name : watchTargets.keySet() ) {
					System.out.println("name[" + name + "]:" + watchTargets.get(name).size() );
				}
				for( String name : watchChannels.keySet() ) {
					System.out.println("name[" + name + "]:" + watchTargets.get(name).size() );
				}
				Thread.sleep(MAX_WAITING_TIME);
			} catch ( Exception e ) {}
		}
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
