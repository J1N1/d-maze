package maze.server;

public class GameTimer {
	
	public class SleepThread implements Runnable {
		private GameTimer game_timer;
		
		public SleepThread(GameTimer game_timer) {
			this.game_timer = game_timer;
		}
		
		public void run() {
			try {
				System.out.println("SleepThread starts sleeping...");
				Thread.sleep(waiting_timeout);
				System.out.println("SleepThread finishes sleeping.");
				game_timer.myNotifyAll();
			} catch (InterruptedException e) {}
		}
	}
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
			System.out.println("First join time is:" + first_join_time);
			new Thread(new SleepThread(this), "SleepThread").start();
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
		System.out.println("GameTimer.isTimeToStart()...");
		while (first_join_time == -1 || 
				System.currentTimeMillis() - first_join_time <= waiting_timeout) {
			try {
				System.out.println("GameTimer.isTimeToStart() start waiting...");
				wait();
			} catch (InterruptedException e) {}
		}
		notifyAll();
		return true;
	}
	
	public synchronized void myNotifyAll() {
		notifyAll();
	}
}
