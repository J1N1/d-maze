package maze.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import maze.play.GameMonitor;
import maze.play.Maze;

public class MazeClient implements GameMonitor{
	public void startGame() {
		
	}
	
	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "MazeServer";
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Maze maze = (Maze) registry.lookup(name);
            
        } catch (Exception e) {
            System.err.println("MazeClient exception: ");
            e.printStackTrace();
        }
	}
}
