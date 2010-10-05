package maze.play;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameMonitor extends Remote{
	public void startGame() throws RemoteException;
	public boolean isPlayerCrash() throws RemoteException;
}
