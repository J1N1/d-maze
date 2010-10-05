package maze.server;

/**
 * use a separate thread to call back the players to start the game
 * @author kodylau
 *
 */
public class CallBackThread extends Thread{
	private MazeData maze_data;
	private GameTimer game_timer;
	
	public CallBackThread(MazeData maze_data, GameTimer game_timer) {
		this.maze_data = maze_data;
		this.game_timer = game_timer;
	}
	
	public void run() {
		System.out.println("CallBackThread start running!");
		if (game_timer.isTimeToStart()) {
			// call back the players
			System.out.println("CallBackThread starts to call back players...");
			maze_data.initPlayerData();
			maze_data.callBackPlayer();
			System.out.println("CallBackThread finishes calling back players!");
		}
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			// check if there is crashed player
			maze_data.checkPlayers();
		}
	}
}
