package maze.server;

import maze.play.Maze;
import maze.play.CurrentGameState;
import maze.play.GameMonitor;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class MazeServer implements Maze{
	
	private MazeData maze_data;
	private GameTimer game_timer;
	
	public MazeServer(int N, int M) {
		maze_data = new MazeData(N, M);
		game_timer = new GameTimer();
	}
	
	public int joinGame(GameMonitor game_monitor) {
		/**
		 * game is already started, can't join anymore
		 */
		if (game_timer.isGameStarted()) {
			return -1;
		}
		return maze_data.addPlayer(game_monitor);
	}
	
	public CurrentGameState move(int id, String direction) {
		return maze_data.move(id, direction);
	}
	
	public void startServer() {
		
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
