package maze.server;

import java.util.Random;
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
	
	public MazeData(int N, int M) {
		this.N = N;
		this.M = M;
		num_of_player = 0;
		player_monitors = new GameMonitor[MAX_PLAYER_NUM];
		
		grid = new int[N][N];
		
		// randomize the treasure locations
		Random random = new Random();
		for (int i = 0; i < M; i++) {
			do {
				int random_int = random.nextInt(N * N);
				int r = random_int / N;
				int c = random_int % N;
				if (grid[r][c] != 1) {
					grid[r][c] = 1;
					break;
				}
			} while (true);
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
				if (grid[r][c] != 1 && grid[r][c] != -1) {
					grid[r][c] = -1;
					location[i].x = r;
					location[i].y = c;
					break;
				}
			} while (true);
		}
		// undo the -1 flag of grid
		for (int i = 0; i < num_of_player; i++) {
			grid[location[i].x][location[i].y] = 0;
		}
		// set all collected number to 0
		for (int i = 0; i < num_of_player; i++) {
			collected[i] = 0;
		}
	}
	
	public synchronized int addPlayer(GameMonitor game_monitor) {
		if (num_of_player >= MAX_PLAYER_NUM) {
			return -1;
		}
		player_monitors[num_of_player++] = game_monitor;
		return num_of_player - 1;
	}
	
	private CurrentGameState getCurrentGameState() {
		return new CurrentGameState(location, collected, grid);
	}
	private boolean isInGrid(int x, int y) {
		return x >= 0 && x < N && y >= 0 && y < N;
	}
	private Pair getNewLocation(Pair loc, String direction) {
		int nx = loc.x, ny = loc.y;
		if (direction == "S") {
			nx = loc.x + 1;
			ny = loc.y;
		} else if (direction == "E") {
			nx = loc.x;
			ny = loc.y + 1;
		} else if (direction == "N") {
			nx = loc.x - 1;
			ny = loc.y;
		} else if (direction == "W") {
			nx = loc.x;
			ny = loc.y - 1;
		} else if (direction == "NoMove") {
			
		} else {
			
		}
		if (isInGrid(nx, ny)) {
			return new Pair(nx, ny);
		} else {
			return loc;
		}
	}
	public synchronized CurrentGameState move(int id, String direction) {
		Pair new_loc = getNewLocation(location[id], direction);
		location[id] = new_loc;
		if (grid[new_loc.x][new_loc.y] == 1) {
			collected[id]++;
			grid[new_loc.x][new_loc.y] = 0;
		}
		return getCurrentGameState();
	}
	public synchronized void callBackPlayer() {
		for (int i = 0; i < num_of_player; i++) {
			player_monitors[i].startGame();
		}
	}
}
