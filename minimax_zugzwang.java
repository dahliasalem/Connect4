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

	private boolean alternate;
	private int myID;
	private int eID;
	private int depth;
	private int countdown;
	private int width;
	private int height;
	private int lastX;
	private int lastY;
	private int[][] myThreats;
	private int[][] eThreats;
	private boolean[] full;
	private int[] heights;
	private static final int BIGWIN = 100000;
	private static final int ILLEGAL = -1000000;
	private static final int NOLOSS = 10000;
	private static final int SUICIDE = -10000;
	private static final int HORIZON = 100;
	private static final int FLANKER = 200;
	private static final int EHORIZON = -9;
	private static final int ROWZUG = 5;

	public minimax_zugzwang() {
		myID = eID = 0;
		depth = 0;
		width = 7;
		height = 6;
		lastX = lastY = -1;
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
				h = heights[i];
				heights[i]++;
				System.out.println("Enemy placed at " + i + "," + h);
				enemyMove = i;
				break;
			}
		}
		if(enemyMove != -1) {
			if(myThreats[enemyMove][h] != 0)
			myThreats[enemyMove][h] = 0;
		}
		findThreats(eID, enemyMove, h, state);
		findThreats(myID, lastX, lastY, state);
		while(!terminate) {
			evaluate(state);
			/*
			for(int i = 0; i < 7; i++) {
				System.out.print("[" + moves[i] + "]");
			}
			System.out.print("\n");
			// */
			countdown = ++depth;
		}
		//printThreats(eThreats);
		lastX = chosenMove;
		lastY = heights[chosenMove];
		eThreats[lastX][lastY] = 0;
		heights[chosenMove]++;
		System.out.println("Depth: " + depth);
		depth = countdown = 0;
	}

	private int[] evaluate(final GameStateModule state) {
		int h = -1;
		moves = new int[7];
		for(int i = 0; i < 7; i++) {
			h = heights[i];
			if(h != 6) {
				if(myThreats[i][h] == myID) {
					moves[i] = Integer.MAX_VALUE; // if we can win no further calcs are necessary
				}
				else{
					if(eThreats[i][h] == eID) {
						moves[i] += NOLOSS; // if our opponent can win we have no choice but to block
					}
					else {
						if(h != 5 && eThreats[i][h+1] == eID) {
							moves[i] += SUICIDE; // don't place the piece that enables your opponent to win
						}
						if((i == 1 || i == 5) && h == 0 && state.getAt(3, 2) == eID) {
							int v = (3-i)/2;
							if(state.getAt(i+v, 1) == eID) {
								moves[i] += FLANKER;
							}
						}
						/*for(int a = 1; a < 6-h; a++) {
							if(myThreats[i][h+a] == myID) {
								moves[i] += a == 1 ? -ROWZUG : ((a+1)%2)*ROWZUG; // don't waste a zug row
								break;
							}
							if(eThreats[i][h+a] == eID) {
								moves[i] += a == 1 ? -ROWZUG : ((a+1)%2)*ROWZUG;
								break;
							}
						}
						if(h < 5) {
							moves[i] -= checkHorizontal(i,h+1, state) == 0 ? 0 : HORIZON*(5-h);
						}// */
						moves[i] += checkHorizontal(i,h,state) == 0 ? 0 : HORIZON*(6-h);
						moves[i] += 4 - Math.abs(i - 3) /*+ (6-h)*/; // this gives a very small bias toward the center bottom
					}
				}
			}
			// else {
			// 	moves[i] += ILLEGAL;
			// }
		}

		if(countdown-- > 0) {
			System.out.print(" | " + countdown + " | ");
			for(int i = 0; i < 7; i++) {
				if(state.canMakeMove(i) && moves[i] != Integer.MAX_VALUE) {
					heights[i]++;
					state.makeMove(i);
					int[] children = evaluate(state);
					state.unMakeMove();
					heights[i]--;
					int min = Integer.MAX_VALUE;
					for(int j = 0; j < 7; j++) {
						if(children[j] < min) {
							min = children[j];
						}
					}
					moves[i] = min;
				}
				// else if(!state.canMakeMove(i)) {
				// 	moves[i] = -Integer.MAX_VALUE;
				// }
			}
		}

		for(int i = 0; i < 7; i++) {
			if(state.canMakeMove(i)) {
				if(alternate) {
					if(moves[i] > moves[chosenMove]) {
						chosenMove = i;
					}
					alternate = false;
				}
				else {
					if(moves[i] >= moves[chosenMove]) {
						chosenMove = i;
					}
					alternate = true;
				}
			}
			else {
				moves[i] = Integer.MAX_VALUE;
			}
		}

		return moves;
	}

	private int checkHorizontal(final int x, final int y, final GameStateModule state) {
		int count1 = 0;
		int count2 = 0;
		//check for cases of 2 matched coins and no blocking coin in every horizontal 4 that x,y could be a part
		for(int i = x - 3; i <= x; i++) {
			count1 = 0;
			count2 = 0;
			if(isLegal(i,y) && isLegal(i+3,y)) {
				for(int d = 0; d < 4; d++) {
					int id = state.getAt(i+d, y);
					switch(id) {
						case 1: count1++;
								break;
						case 2: count2++;
								break;
					}
				}
				if(count1 > 1 && count2 == 0) {
					return 1;
				}
				if(count1 == 0 && count2 > 1) {
					return 2;
				}
			}
		}
		return 0;
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
		if(y > 1 && y < 5) {
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
	}

	private boolean isLegal(final int x, final int y) {
		return y >= 0 && y <= 5 && x >= 0 && x <= 6;
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
