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
	
	public MazeServer(MazeData maze_data, GameTimer game_timer) {
		this.maze_data = maze_data;
		this.game_timer = game_timer;
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
	
	public static void main(String args[]) {
//		if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        try {
            String name = "MazeServer";
            Maze maze= null;
            MazeData maze_data = null;
            GameTimer game_timer = null;
            
            if (args.length != 2) {
            	System.out.println("Usage: maze N M");
            	System.out.println("This command will start a game server with N*N grid and M treasures.");
            	System.exit(1);
            } else {
            	try {
            		maze_data = new MazeData(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            		game_timer = new GameTimer();
            		maze = new MazeServer(maze_data, game_timer);
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
            
            Thread call_backer = new CallBackThread(maze_data, game_timer);
            call_backer.setName("CallBackThread");
            call_backer.start();
            
        } catch (Exception e) {
            System.err.println("MazeServer exception:");
            e.printStackTrace();
        }
	}
}
