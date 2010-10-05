package maze.play;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defining the remote interface between client and server
 */
public interface Maze extends Remote {
	/**
	 * 
	 * @return the id of the player, generated by the server.
	 */
	public int joinGame(GameMonitor game_monitor) throws RemoteException;
	/**
	 * @param id
	 * @param direction
	 * @return the current game state
	 */
	public CurrentGameState move(int id, String direction) throws RemoteException;
}
