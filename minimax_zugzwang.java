import java.awt.Point;

/**
 * Rules for our AI:
 * 1: If we have a winning move, take it
 * 2: If opponent has a winning move, block it
 * 3: If I can generate a fork (two ways to win) after this move, do it
 * 4: If opponent can generate a fork, prevent it
 * 5: Find position which has most possible winning outcomes and place there
 * 
 * 
 * @author Stefan Peterson, Benjamin Roye
 * @see RandomAI, StupidAI, MonteCarloAI
 */

public class minimax_zugzwang extends AIModule
{
	private int[] moves;

	private int myID;
	private int eID;
	private int depth;
	private int width;
	private int height;
	private int[][] threats;
	private boolean[] full;
	private int[] heights;
	private static final int BIGWIN = 100000;
	private static final int NOLOSS = 1000;
	private static final int NEWTHREAT = 100;

	public minimax_zugzwang() {
		myID = eID = 0;
		depth = 0;
		width = 7;
		height = 6;
		myThreats = new int[width][height];
		eThreats = new int[width][height];
		heights = new int[width];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				myThreats[i][j] = 0;
				eThreats[i][j] = 0;
			}
		}
		for(int i = 0; i < width; i++) {
			heights[i] = 0;
		}
	}

	public void getNextMove(final GameStateModule state){
		if(!myID) {
			myID = state.getActivePlayer();
			eID = myID == 1 ? 2 : 1;
		}
		//only uncomment these if we have reason to believe there will be non-standard sizes
		//width = state.getWidth();
		//height = state.getHeight();
		//this is an array the width of the board that symbolizes each possible move
		moves = new int[width];
		chosenMove = 0;
		int enemyMove = -1;
		int h = 0;
		// This loop determines where the enemy played
		for(int i = 0; i < width; i++) {
			h = heights[i];
			if(h != height && h != state.getHeightAt(i)) {
				System.out.println("Enemy placed at " + i);
				enemyMove = i;
				heights[i]++;
				h = heights[i];
				break;
			}
		}
		while(!terminate) {
			if(h > 2) {
				// has a vertical threat been created?
				if(state.getAt(enemyMove, h-1) == eID && state.getAt(enemyMove, h-2) == eID) {
					eThreats[enemyMove][h+1] = eID;
				}
				if(enemyMove > 0 && enemyMove < 5 && state.getAt(enemyMove+1, h-1) == eID && state.getAt(enemyMove+2, h-2)) {
					eThreats[enemyMove-1][h+1] = eID;
				}
			}
		}
		heights[chosenMove]++;
		depth = 0;
	}
}
