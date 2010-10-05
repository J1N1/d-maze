package maze.play;

import java.io.Serializable;

public class Pair implements Serializable {
	private static final long serialVersionUID = 229L;
	
	public int x, y;
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
