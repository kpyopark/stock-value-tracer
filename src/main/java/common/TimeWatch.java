package common;

public class TimeWatch {
	private long startTime = 0;
	private long endTime = 0;
	private long cnt = 0;
	private long meanTime = 0;
	public TimeWatch() {
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		endTime = System.currentTimeMillis();
		meanTime = ( ( endTime - startTime ) + cnt * meanTime ) / (cnt + 1);
		cnt++;
		System.out.println("elapsed time:" + ( endTime - startTime));
		System.out.println("elapsed mean time:" + meanTime );
	}
	
	public void stopAndStart() {
		stop();
		start();
	}
	
	public void reset() {
		this.cnt = 0;
		this.meanTime = 0;
	}

}
