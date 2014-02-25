import java.awt.Point;
import java.util.Random;
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
	private final Random r = new Random(System.currentTimeMillis());
	private int[] moves;

	private int myID;
	private int eID;
	private int depth;
	private int width;
	private int height;
	private int[][] myThreats;
	private int[][] eThreats;
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
		if(myID == 0) {
			myID = state.getActivePlayer();
			eID = myID == 1 ? 2 : 1;
		}
		//only uncomment these if we have reason to believe there will be non-standard sizes
		//width = state.getWidth();
		//height = state.getHeight();
		//this is an array the width of the board that symbolizes each possible move
		moves = new int[width];
		chosenMove = r.nextInt(7);
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
		if(enemyMove != -1) {
			myThreats[enemyMove][h] = 0;
		}
		while(!terminate) {
			findThreats(eID, enemyMove, h, state);
		}
		printThreats(eThreats);
		eThreats[chosenMove][state.getHeightAt(chosenMove)] = 0;
		heights[chosenMove]++;
		depth = 0;
	}

	private void findThreats(final int player, final int x, final int y, final GameStateModule state) {
		for(int i = x - 3; i <= x; i++) {
			int p = -1;
			int c = 0;
			int blank = -1;
			// BEGIN horizontal
			if(isLegal(i,y) && isLegal(i+3,y)) {
				
				for(int d = 0; d < 4; d++) {
					p = state.getAt(i+d, y);
					if(p == player) {
						c++;
					}
					else if(p == 0) {
						blank = d;
					}
				}
				if(c == 3 && blank != -1) {
					if(player == myID) {
						myThreats[i+blank][y] = myID;
					}
					else {
						eThreats[i+blank][y] = eID;
					}
				}
			}
			// END horizontal
			// BEGIN agu
			for(int j = y - 3; j <= y; j++) {
				if(isLegal(i,j) && isLegal(i+3,j+3)) {
					p = -1;
					c = 0;
					blank = -1;
					for(int d = 0; d < 4; d++) {
						p = state.getAt(i+d, j+d);
						if(p == player) {
							c++;
						}
						else if(p == 0) {
							blank = d;
						}
					}
					if(c == 3 && blank != -1) {
						if(player == myID) {
							myThreats[i+blank][j+blank] = myID;
						}
						else {
							eThreats[i+blank][j+blank] = eID;
						}
					}
				}
			}
			// END agu
			// BEGIN grave
			for(int k = y + 3; k >= y; k--) {
				if(isLegal(i,k) && isLegal(i+3,k-3)) {
					p = -1;
					c = 0;
					blank = -1;
					for(int d = 0; d < 4; d++) {
						p = state.getAt(i+d, k-d);
						if(p == player) {
							c++;
						}
						else if(p == 0) {
							blank = d;
						}
					}
					if(c == 3 && blank != -1) {
						if(player == myID) {
							myThreats[i+blank][k-blank] = myID;
						}
						else {
							eThreats[i+blank][k-blank] = eID;
						}
					}
				}
			}
			// END grave
		}
		// BEGIN vertical
		if(y > 2 && y < 5) {
			if(state.getAt(x, y-1) == player && state.getAt(x, y-2) == player) {
				if(player == myID) {
					myThreats[x][y+1] = myID;
				}
				else {
					eThreats[x][y+1] = eID;
				}
			}
		}
		// END vertical
		/*
			if(y > 2 && y < 6) { //check for threats above
				// has a vertical threat been created?
				if(state.getAt(x, y-1) == player && state.getAt(x, y-2) == player) {
					eThreats[x][y+1] = player;
				}
				// has a diagonal threat been created to the left?
				if(x > 0 && x < 5 
					&& state.getAt(x+1, y-1) == player 
					&& state.getAt(x+2, y-2) == player 
					&& state.getAt(x-1, y+1) == 0) {
					eThreats[x-1][y+1] = player;
				}
				// has a diagonal threat been created to the right?
				if(x > 1 && x < 6
					&& state.getAt(x-1, y-1) == player
					&& state.getAt(x-2, y-2) == player
					&& state.getAt(x+1, y+1) == 0) {
					eThreats[x+1][y+1] = player;
				}
			}
			if(y > 0 && y <)
		*/
	}

	private boolean isLegal(int x, int y) {
		return x >= 0 && x <= 6 && y >= 0 && y <= 5;
	}

	private void printThreats(final int[][] a) {
		for(int i = 5; i > -1; i--) {
			for(int j = 0; j < 7; j++) {
				System.out.print("[" + a[j][i] + "]");
			}
			System.out.print("\n");
		}
	}
}
