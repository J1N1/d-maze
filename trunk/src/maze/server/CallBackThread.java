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
		if (game_timer.isTimeToStart()) {
			// call back the players
			maze_data.callBackPlayer();
		}
	}
}
