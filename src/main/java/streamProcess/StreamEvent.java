package streamProcess;

public class StreamEvent {
	
	public final static int EVENT_QUEUE_EMPTY = 0;
	public final static int EVENT_QUEUE_FULL = 0;
	
	private int type = 0;
	
	public int getType() {
		return type;
	}

	public StreamEvent(int type) {
		this.type = type;
	}
	
}
