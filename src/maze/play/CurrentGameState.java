package maze.play;

import java.io.Serializable;

public class CurrentGameState  implements Serializable {
	private static final long serialVersionUID = 228L;
	
	public int N, num_of_player;
	public Pair[] location;
	public int[] collected;
	public int[][] grid;
	
	public CurrentGameState(Pair[] location, int[] collected, int[][] grid, int N, int num_of_player) {
		this.location = location;
		this.collected = collected;
		this.grid = grid;
		this.N = N;
		this.num_of_player = num_of_player;
	}
}