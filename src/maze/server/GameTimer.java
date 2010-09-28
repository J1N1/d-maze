package maze.server;

public class GameTimer {
	/**
	 * Game starts 20s after the first joinGame request.
	 */
	private static long waiting_timeout = 20000;
	private long first_join_time;
	
	public GameTimer() {
		first_join_time = -1;
	}
	
	public synchronized boolean isGameStarted() {
		if (first_join_time == -1) {
			first_join_time = System.currentTimeMillis();
			return false;
		} else {
			if (System.currentTimeMillis() - first_join_time > waiting_timeout) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public synchronized boolean isTimeToStart() {
		while (first_join_time == -1 || 
				System.currentTimeMillis() - first_join_time <= waiting_timeout) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		notifyAll();
		return true;
	}
}
