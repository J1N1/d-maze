package maze.play;

public class CurrentGameState {
	public Pair[] location;
	public int[] collected;
	public int[][] grid;
	
	public CurrentGameState(Pair[] location, int[] collected, int[][] grid) {
		this.location = location;
		this.collected = collected;
		this.grid = grid;
	}
}