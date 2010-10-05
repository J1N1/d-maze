package maze.client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import maze.play.*;

public class MazeClient implements GameMonitor, Serializable{
	
	private static final long serialVersionUID = 227L;
	
	public Maze maze;
	public CurrentGameState game_data;
	private int id;
	private boolean start;
	
	public MazeClient(Maze maze) {
		this.maze = maze;
		start = false;
		try {
			UnicastRemoteObject.exportObject(this, 0);
		} catch (RemoteException e) {
			System.out.println("Fails to allow the client to be call backed.");
			e.printStackTrace();
		}
	}
	
	public void joinGame() {
		try {
			id = maze.joinGame(this);
			if (id == -1) {
				System.err.println("Game has started, can't join.");
				System.exit(1);
			} else {
				System.out.println("Waiting for the game to start...");
			}
		} catch (RemoteException e) {
			System.out.println("Join Game Exception:");
			e.printStackTrace();
		}
	}
	
	public void move(int id, String direction) {
		try {
			game_data = maze.move(id, direction);
			drawScreen();
		} catch (RemoteException e) {
			System.out.println("Move Exception:");
			e.printStackTrace();
		}
	}
	
	public synchronized void startGame() {
		start = true;
		notifyAll();
	}
	
	private int treasuresLeft() {
		int ret = 0;
		for (int i = 0; i < game_data.N; i++) {
			for (int j = 0; j < game_data.N; j++) {
				if (game_data.grid[i][j] == 1) ret++;
			}
		}
		return ret;
	}
	private void drawScreen() {
		System.out.println("You: * \t The others: x \t Blank grid: 0 \t Treasure: $");
		System.out.println("Your treasures:" + game_data.collected[id] + "\t Treasures left:" + treasuresLeft());
		System.out.println("Your location: (" + game_data.location[id].x + ", " + game_data.location[id].y + ")" +
				"\t Number of Players: " + game_data.num_of_player);
		System.out.println();
		char[][] grid = new char[game_data.N][game_data.N];
		for (int i = 0; i < game_data.N; i++) {
			for (int j = 0; j < game_data.N; j++) {
				if (game_data.grid[i][j] == 1) {
					grid[i][j] = '$';
				} else {
					grid[i][j] = '0';
				}
			}
		}
		for (int i = 0; i < game_data.num_of_player; i++) {
			int x = game_data.location[i].x;
			int y = game_data.location[i].y;
			grid[x][y] = id == i ? '*' : 'x';
		}
		for (int i = 0; i < game_data.N; i++) {
			for (int j = 0; j < game_data.N; j++) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private int dist(Pair loc1, Pair loc2) {
		return Math.abs(loc1.x - loc2.x) + Math.abs(loc1.y - loc2.y);
	}
	
	private String nextMove() {
		int[][] dir = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
		String[] direction = {"E", "W", "S", "N"};
		Random random = new Random();
		Pair loc = game_data.location[id];
		
		// if its neighbor is treasure, move there
		for (int i = 0; i < 4; i++) {
			int r = loc.x + dir[i][0];
			int c = loc.y + dir[i][1];
			if (r >= 0 && r < game_data.N && 
					c >= 0 && c < game_data.N) {
				if (game_data.grid[r][c] == 1) return direction[i]; 
			}
		}
		
		int d = random.nextInt(4);
		Pair new_loc = new Pair(loc.x + dir[d][0], loc.y + dir[d][1]);
		for (int i = 0; i < game_data.N; i++) {
			for (int j = 0; j < game_data.N; j++) {
				if (game_data.grid[i][j] == 1) {
					Pair treasure_loc = new Pair(i, j);
					if (dist(new_loc, treasure_loc) < dist(loc, treasure_loc)) {
						return direction[d];
					}
				}
			}
		}
		return "NoMove";
	}
	public synchronized void playGame() throws InterruptedException {
		Random random = new Random();
		
		while (!start) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		move(id, "NoMove");
		Thread.sleep(5000);
		while (treasuresLeft() > 0) {
			move(id, nextMove());
			Thread.sleep(random.nextInt(5) * 1000 + 1000);
		}
		System.out.println("Game Over!");
	}
	
	public static void main(String args[]) {
//		if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        try {
            String name = "MazeServer";
            Registry registry = LocateRegistry.getRegistry(args[0]);
            Maze maze = (Maze) registry.lookup(name);
            MazeClient maze_client = new MazeClient(maze);
            maze_client.joinGame();
            maze_client.playGame();            
        } catch (Exception e) {
            System.err.println("MazeClient exception: ");
            e.printStackTrace();
        }
	}
}
