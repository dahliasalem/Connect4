import java.util.Random;
public class minimax_bs extends AIModule
{
	private final Random r = new Random(System.currentTimeMillis());
	private int[] moves;
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
	private static final int THREEFER = 6;
	private static final int TWOFER = 2;
	private static final int DEPTHLIMIT = 4;

	public minimax_bs() {
		myID = eID = 0;
		depth = 0;
		width = 7;
		height = 6;
		lastX = lastY = -1;
		myThreats = new int[width][height];
		eThreats = new int[width][height];
		moves = new int[width];

		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				myThreats[i][j] = 0;
				eThreats[i][j] = 0;
			}
		}
	}

	public void getNextMove(final GameStateModule state) {
		chosenMove = r.nextInt(7);
		evaluate(state);
		depth = 0;
	}

	private int evaluate(final GameStateModule state) {
		depth++;
		if(myID == 0) {
			myID = state.getActivePlayer();
		}
		else {
			myID = myID == 1 ? 2 : 1;
		}
		eID = myID == 1 ? 2 : 1;
		int my2s = 0;
		int my3s = 0;
		int e2s = 0;
		int e3s = 0;
		int myCount = 0;
		int eCount = 0;
		if(state.isGameOver()) {
			if(state.getWinner() == myID) {
				System.out.println("Player "+myID+" Depth: "+depth+" WIN");
				depth--;
				return Integer.MAX_VALUE;
			}
			else if(state.getWinner() == eID) {
				System.out.println("Player "+myID+" Depth: "+depth+" LOSE");
				depth--;
				return -Integer.MAX_VALUE;
			}
		}
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 6; j++) {
				myCount = 0;
				eCount = 0;
				for(int d = 0; d < 4; d++) {
					int p = state.getAt(i+d,j);
					if(p == myID) {
						myCount++;
					}
					else if(p == eID) {
						eCount++;
					}
				}
				if(myCount == 0) {
					if(eCount == 2) {
						//System.out.println("Enemy2 from "+i+","+j+" to "+(i+3)+","+j);
						e2s++;
					}
					else if(eCount == 3) {
						//System.out.println("Enemy3 from "+i+","+j+" to "+(i+3)+","+j);
						e3s++;
					}
				}
				if(eCount == 0) {
					if(myCount == 2) {
						//System.out.println("My2 from "+i+","+j+" to "+(i+3)+","+j);
						my2s++;
					}
					else if(myCount == 3) {
						//System.out.println("My3 from "+i+","+j+" to "+(i+3)+","+j);
						my3s++;
					}
				}
			}
		}

		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 3; j++) {
				myCount = 0;
				eCount = 0;
				for(int d = 0; d < 4; d++) {
					int p = state.getAt(i+d,j+d);
					if(p == myID) {
						myCount++;
					}
					else if(p == eID) {
						eCount++;
					}
				}
				if(myCount == 0) {
					if(eCount == 2) {
						//System.out.println("Enemy2 agu from "+i+","+j+" to "+(i+3)+","+(j+3));
						e2s++;
					}
					else if(eCount == 3) {
						//System.out.println("Enemy3 agu from "+i+","+j+" to "+(i+3)+","+(j+3));
						e3s++;
					}
				}
				if(eCount == 0) {
					if(myCount == 2) {
						//System.out.println("My2 agu from "+i+","+j+" to "+(i+3)+","+(j+3));
						my2s++;
					}
					else if(myCount == 3) {
						//System.out.println("My3 agu from "+i+","+j+" to "+(i+3)+","+(j+3));
						my3s++;
					}
				}
			}
		}


		for(int i = 0; i < 4; i++) {
			for(int j = 3; j < 6; j++) {
				myCount = 0;
				eCount = 0;
				for(int d = 0; d < 4; d++) {
					int p = state.getAt(i+d,j-d);
					if(p == myID) {
						myCount++;
					}
					else if(p == eID) {
						eCount++;
					}
				}
				if(myCount == 0) {
					if(eCount == 2) {
						//System.out.println("Enemy2 grav from "+i+","+j+" to "+(i+3)+","+(j-3));
						e2s++;
					}
					else if(eCount == 3) {
						//System.out.println("Enemy3 grav from "+i+","+j+" to "+(i+3)+","+(j-3));
						e3s++;
					}
				}
				if(eCount == 0) {
					if(myCount == 2) {
						//System.out.println("My2 grav from "+i+","+j+" to "+(i+3)+","+(j-3));
						my2s++;
					}
					else if(myCount == 3) {
						//System.out.println("My3 grav from "+i+","+j+" to "+(i+3)+","+(j-3));
						my3s++;
					}
				}
			}
		}

		for(int i = 0; i < 7; i++) {
			int topPlayer = 0;
			int count = 0;
			for(int j = 0; j < 6; j++) {
				int p = state.getAt(i,j);
				if(p == 0)
					break;
				if(p == topPlayer) {
					count++;
				}
				else {
					topPlayer = p;
					count = 1;
				}
			}
			if(count == 2) {
				if(topPlayer == myID) {
					//System.out.println("My2 in column "+i);
					++my2s;
				}
				else {
					//System.out.println("Enemy2 in column "+i);
					++e2s;
				}
			}
			else if(count == 3) {
				if(topPlayer == myID) {
					//System.out.println("My3 in column "+i);
					++my3s;
				}
				else {
					//System.out.println("Enemy3 in column "+i);
					++e3s;
				}
			}
		}

		if(depth < DEPTHLIMIT && !terminate) {
			int min = Integer.MAX_VALUE;
			int minMove = -1;
			int rememberID = myID;
			moves = new int[7];
			for(int i = 0; i < 7; i++) {
				if(state.canMakeMove(i)) {
					state.makeMove(i);
					moves[i] = evaluate(state);
					myID = rememberID;
					state.unMakeMove();
				}
			}
			for(int i = 0; i < 7; i++) {
				if(moves[i] < min) {
					min = moves[i];
					minMove = i;
				}
			}
			chosenMove = minMove;
			System.out.println("Player "+myID+" Depth: "+depth+" minimum");
			depth--;
			return moves[minMove];
		}

		// System.out.println(
		// 	"Player: " + myID + "\tDepth: " + depth + "\tMyTwos:" + my2s + " MyThrees:" + my3s + " eTwos:" + e2s + " eThrees:" + e3s
		// );
		System.out.println("Player "+myID+" Depth: "+depth+" evaluate");
		depth--;
		return (my2s*TWOFER + my3s*THREEFER) - (e2s*TWOFER + e3s*THREEFER);
	}

	private boolean isLegal(final int x, final int y) {
		return y >= 0 && y <= 5 && x >= 0 && x <= 6;
	}
}