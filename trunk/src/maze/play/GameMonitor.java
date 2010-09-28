package maze.play;

import java.rmi.Remote;

public interface GameMonitor extends Remote{
	public void startGame();
}
