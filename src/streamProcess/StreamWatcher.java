package streamProcess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import common.QueueUtil;

public class StreamWatcher implements Runnable {
	
	HashMap<String, ObservableChannel> watchTargets = new HashMap<String,ObservableChannel>();
	final static int MAX_WAITING_TIME = 2000;

	boolean needExit = false;
	static StreamWatcher singletone = null;
	Thread currentThread = null;
	
	private StreamWatcher() {
	}
	
	class ConnectionAndChannel {
		public Connection con;
		public Channel channel;
		String queueName;
		public ConnectionAndChannel(Connection con, Channel chan, String queueName) {
			ConnectionAndChannel.this.con = con;
			ConnectionAndChannel.this.channel = chan;
			ConnectionAndChannel.this.queueName = queueName;
		}
	}
	
	class ObservableChannel {
		public List<StreamEventListener> listeners = new ArrayList<StreamEventListener>();
		public BlockingQueue watchTargetQueue = null;
		public ConnectionAndChannel watchTargetChannel = null;
		public ObservableChannel(BlockingQueue queue) {
			this.watchTargetQueue = queue;
		}
		public ObservableChannel(ConnectionAndChannel queue) {
			this.watchTargetChannel = queue;
		}
		public void addListener(StreamEventListener listener) {
			listeners.add(listener);
		}
		public void removeListener(StreamEventListener listener) {
			listeners.remove(listener);
		}
		public void removeAllListeners() {
			listeners.clear();
		}
		public void fireEvent(StreamEvent event) {
			for(StreamEventListener listener:listeners) {
				listener.eventHandler(event);
			}
		}
		public int getQueueSize() throws IOException {
			if ( watchTargetQueue != null ) {
				return watchTargetQueue.size();
			}
			if ( watchTargetChannel != null ) {
				Channel channel = watchTargetChannel.channel;
				return channel.queueDeclarePassive(watchTargetChannel.queueName).getMessageCount();
			}
			return 0;
		}
		
	}
	
	public static StreamWatcher getStreamWatcher() {
		if ( singletone == null ) {
			singletone = new StreamWatcher();
		}
		return singletone;
	}
	
	public void addStreamEventListener(String watchTarget, StreamEventListener listener) {
		if ( watchTargets.containsKey(watchTarget)) {
			watchTargets.get(watchTarget).addListener(listener);
		}
	}
	
	public void removeStreamEventListener(String watchTarget, StreamEventListener listener) {
		if ( watchTargets.containsKey(watchTarget)) {
			watchTargets.get(watchTarget).removeListener(listener);
		}
	}
	
	private void removeAllEventListener() {
		for(ObservableChannel watchTarget:watchTargets.values()) {
			watchTarget.removeAllListeners();
		}
	}
	
	private void removeAllWatchTargets() {
		removeAllEventListener();
		watchTargets.clear();
	}
	
	private ConnectionAndChannel createChannel(String queueName) throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection con = factory.newConnection();
		Channel channel = con.createChannel();
		return new ConnectionAndChannel(con, channel, queueName);
	}
	
	public void addWatchTarget(String name,BlockingQueue target) {
		watchTargets.put(name, new ObservableChannel(target));
		if ( currentThread == null )
			start();
	}

	public void addWatchTarget(String name) {
		try {
			watchTargets.put(name, new ObservableChannel(createChannel(name)));
			if ( currentThread == null )
				start();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// Skip.
		}
	}
	
	public void removeWatchTarget(String name) {
		ObservableChannel target = watchTargets.remove(name);
		target.removeAllListeners();
		if (target.watchTargetChannel != null ) {
			ConnectionAndChannel cac = target.watchTargetChannel;
			try {
				cac.channel.close();
			} catch (IOException ioe) {
				// skip.
			} finally {
				try { cac.con.close(); } catch (IOException ioe) {}
			}
		}
		if ( watchTargets.isEmpty())
			stop();
	}
	
	public void run() {
		while(!needExit) {
			try {
				for( String name : watchTargets.keySet() ) {
					int queueSize = 0;
					ObservableChannel channel = watchTargets.get(name);
					System.out.println("name[" + name + "]:" + (queueSize = channel.getQueueSize()));
					if (queueSize == 0) {
						StreamEvent event = new StreamEvent(StreamEvent.EVENT_QUEUE_EMPTY);
						channel.fireEvent(event);
					}
				}
				Thread.sleep(MAX_WAITING_TIME);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
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
		removeAllEventListener();
		removeAllWatchTargets();
	}

}
