package maze.server;

import maze.play.Maze;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class MazeServer implements Maze{
	/**
	 * N * N grid with M treasures.
	 */
	final private int N, M;
	
	/**
	 * Game starts 20s after the first joinGame request.
	 */
	private static long waiting_timeout = 20000;
	private long first_join_time;
	
	public MazeServer(int N, int M) {
		this.N = N;
		this.M = M;
	}
	
	public int joinGame() {
		if (System.currentTimeMillis() - first_join_time > waiting_timeout) {
			return -1;		// -1 indicates fail to join.
		}
	}
	
	public CurrentGameState move(int id, String direction) {
		
	}
	
	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "MazeServer";
            MazeServer maze_server = null;
            
            if (args.length != 2) {
            	System.out.println("Usage: maze-server N M");
            	System.out.println("This command will start a game server with N*N grid and M treasures.");
            	System.exit(1);
            } else {
            	try {
            		maze_server = new MazeServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            	} catch (NumberFormatException e) {
            		System.err.println("Arguments must be integers.");
            		System.exit(1);
            	}
            }
            
            MazeServer stub =
                (MazeServer) UnicastRemoteObject.exportObject(maze_server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("MazeServer bound");
        } catch (Exception e) {
            System.err.println("MazeServer exception:");
            e.printStackTrace();
        }
	}
}
