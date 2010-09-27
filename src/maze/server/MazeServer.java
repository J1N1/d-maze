package maze.server;

import maze.play.Maze;
import maze.play.CurrentGameState;
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
	
	private MazeData maze_data;
	
	public MazeServer(int N, int M) {
		this.N = N;
		this.M = M;
	}
	
	public int joinGame(GameMonitor game_monitor) {
		if (System.currentTimeMillis() - first_join_time > waiting_timeout) {
			return -1;		// -1 indicates fail to join.
		}
	}
	
	public CurrentGameState move(int id, String direction) {
		return maze_data.move(id, direction);
	}
	
	public static void main(String args[]) {
		if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "MazeServer";
            Maze maze= null;
            
            if (args.length != 2) {
            	System.out.println("Usage: maze N M");
            	System.out.println("This command will start a game server with N*N grid and M treasures.");
            	System.exit(1);
            } else {
            	try {
            		maze = new MazeServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            	} catch (NumberFormatException e) {
            		System.err.println("Arguments must be integers.");
            		System.exit(1);
            	}
            }
            
            Maze stub =
                (Maze) UnicastRemoteObject.exportObject(maze, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("MazeServer bound");
        } catch (Exception e) {
            System.err.println("MazeServer exception:");
            e.printStackTrace();
        }
	}
}
