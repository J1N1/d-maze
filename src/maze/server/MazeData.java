package maze.server;

import java.util.Random;
import java.rmi.RemoteException;
import maze.play.CurrentGameState;
import maze.play.Pair;
import maze.play.GameMonitor;

public class MazeData {
	final static int MAX_PLAYER_NUM = 128;
	
	/**
	 * NxN grid, M treasures
	 */
	private int num_of_player, N, M;
	private Pair[] location;
	private int[] collected;
	private int[][] grid;
	private GameMonitor[] player_monitors;
	private long[] last_move_time;
	
	public MazeData(int N, int M) {
		this.N = N;
		this.M = M;
		num_of_player = 0;
		player_monitors = new GameMonitor[MAX_PLAYER_NUM];
		last_move_time = new long[MAX_PLAYER_NUM];
		
		grid = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				grid[i][j] = 0;
			}
		}
		// randomize the treasure locations
		Random random = new Random();
		for (int i = 0; i < M; i++) {
			int random_int = random.nextInt(N * N);
			int r = random_int / N;
			int c = random_int % N;
			grid[r][c]++;
		}
	}
	public synchronized void initPlayerData() {
		location = new Pair[num_of_player];
		collected = new int[num_of_player];
		
		// randomize players' locations
		Random random = new Random();
		for (int i = 0; i < num_of_player; i++) {
			do {
				int random_int = random.nextInt(N * N);
				int r = random_int / N;
				int c = random_int % N;
				if (grid[r][c] == 0 && grid[r][c] != -1) {
					grid[r][c] = -1;
					location[i] = new Pair(r, c);
					break;
				}
			} while (true);
		}
		System.out.println("Players location generated!");
		// undo the -1 flag of grid
		for (int i = 0; i < num_of_player; i++) {
			grid[location[i].x][location[i].y] = 0;
		}
		// set all collected number to 0
		for (int i = 0; i < num_of_player; i++) {
			collected[i] = 0;
		}
	}
	private CurrentGameState getCurrentGameState() {
		return new CurrentGameState(location, collected, grid, N, num_of_player);
	}
	private boolean isInGrid(int x, int y) {
		return x >= 0 && x < N && y >= 0 && y < N;
	}
	private Pair getNewLocation(Pair loc, String direction) {
		int nx = loc.x, ny = loc.y;
		if (direction.equals("S")) {
			nx = loc.x + 1;
			ny = loc.y;
		} else if (direction.equals("E")) {
			nx = loc.x;
			ny = loc.y + 1;
		} else if (direction.equals("N")) {
			nx = loc.x - 1;
			ny = loc.y;
		} else if (direction.equals("W")) {
			nx = loc.x;
			ny = loc.y - 1;
		} else if (direction.equals("NoMove")) {
			
		} else {
			
		}
		if (isInGrid(nx, ny)) {
			return new Pair(nx, ny);
		} else {
			return loc;
		}
	}
	
	
	public synchronized int addPlayer(GameMonitor game_monitor) {
		if (num_of_player >= MAX_PLAYER_NUM) {
			return -1;
		}
		player_monitors[num_of_player++] = game_monitor;
		System.out.println("New player joined, ID: " + (num_of_player - 1));
		return num_of_player - 1;
	}
	public synchronized CurrentGameState move(int id, String direction) {
		Pair new_loc = getNewLocation(location[id], direction);
		location[id] = new_loc;
		if (grid[new_loc.x][new_loc.y] > 0) {
			collected[id] += grid[new_loc.x][new_loc.y];
			grid[new_loc.x][new_loc.y] = 0;
		}
		System.out.println("Player " + id + " moves " + direction);
		last_move_time[id] = System.currentTimeMillis();
		CurrentGameState ret = getCurrentGameState();
		return ret;
	}
	public synchronized void checkPlayers() {
		System.out.println("Checking if any player crashes...");
		for (int i = 0; i < num_of_player; i++) {	// num_of_player may change here due to player crash
			try {
				player_monitors[i].isPlayerCrash();
			} catch (RemoteException e) {
				System.out.println("Player " + i + " Crashed!");
				deletePlayer(i);
			}
		}
		System.out.println("Finishes checking!");
	}
	public synchronized void callBackPlayer() {
		System.out.println("Start to call back players...");
		for (int i = 0; i < num_of_player; i++) {	// num_of_player may change here due to player crash
			try {
				player_monitors[i].startGame();
				System.out.println("Call back player " + i);
			} catch (RemoteException e) {
				System.out.println("Player " + i + " Exception: Crash!");
				deletePlayer(i);
			}
		}
	}
	private void deletePlayer(int id) {
		for (int i = id; i < num_of_player - 1; i++) {
			location[i] = location[i+1];
			collected[i] = collected[i+1];
			player_monitors[i] = player_monitors[i+1];
			last_move_time[i] = last_move_time[i+1];
		}
		num_of_player--;
	}
}
