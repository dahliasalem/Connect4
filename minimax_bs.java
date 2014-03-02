import java.util.Random;
/*
*	This was my first try at minimax with a simple evaluation function.
*	The primary difference between this and minimax_py is that this doesn't use
*	gamestate copies, choosing instead to rely on the provided makeMove() and
*	unmakeMove().
*
*	Another notable difference is that this counts up as it gets deeper in the
*	tree while minimax_py counts down as it gets deeper.
*/
public class minimax_bs extends AIModule
{
	private final Random r = new Random(System.currentTimeMillis());
	private int[] moves;
	private int myID;
	private int eID;
	private int depth;
	private int cap;
	private int countdown;
	private int width;
	private int height;
	private int lastX;
	private int lastY;
	private int[][] myThreats;
	private int[][] eThreats;
	private boolean[] full;
	private int[] heights;
	private static final int THREEFER = 100;
	private static final int TWOFER = 1;
	private static final int DEPTHLIMIT = 5;

	public minimax_bs() {
		myID = eID = 0;
		depth = 0;
		width = 7;
		height = 6;
	}

	public void getNextMove(final GameStateModule state) {
		chosenMove = 0; //r.nextInt(7);
		cap = 1;
		while(!terminate) {
			evaluate(state, -1);
			cap++;
		}
		depth = 0;
	}

	// The main recursive function in which chosenMove is assigned
	private int evaluate(final GameStateModule state, final int child) {
		depth++;
		//if(myID == 0) {
			myID = state.getActivePlayer();
		//}
		// else {
		// 	myID = myID == 1 ? 2 : 1;
		// }
		//System.out.println("IN Depth:"+depth+" Player:"+myID+" Child:"+child);
		eID = myID == 1 ? 2 : 1;
		int my2s = 0;
		int my3s = 0;
		int e2s = 0;
		int e3s = 0;
		int myCount = 0;
		int eCount = 0;
		// has someone won in this state?
		if(state.isGameOver()) {
			if(state.getWinner() == myID) {
				//System.out.println("Player "+myID+" Depth: "+depth+" WIN");
				depth--;
				return Integer.MAX_VALUE;
			}
			else if(state.getWinner() == eID) {
				//System.out.println("Player "+myID+" Depth: "+depth+" LOSE");
				depth--;
				return -Integer.MAX_VALUE;
			}
		}
		
		// loop that counts horizontal potential wins
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
					else if(eCount == 4) {
						depth--;
						return -Integer.MAX_VALUE;
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
					else if(myCount == 4) {
						depth--;
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// loop that counts positive slope diagonal potential wins
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
					else if(eCount == 4) {
						depth--;
						return -Integer.MAX_VALUE;
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
					else if(myCount == 4) {
						depth--;
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// loop that counts negative slope diagonal potential wins
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
					else if(eCount == 4) {
						depth--;
						return -Integer.MAX_VALUE;
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
					else if(myCount == 4) {
						depth--;
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// loop that counts potential vertical wins
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
			else if(count == 4) {
				if(topPlayer == myID) {
					depth--;
					return Integer.MAX_VALUE;
				}
				else {
					depth--;
					return -Integer.MAX_VALUE;
				}
			}
		}

		// if we haven't reached the iterative deepening cap and we haven't been
		// told to terminate then recurse a level lower instead of evaluating
		if(depth < cap && !terminate) {
			int max = -Integer.MAX_VALUE;
			int maxMove = -1;
			//int rememberID = myID;
			moves = new int[7];
			for(int i = 0; i < 7; i++) {
				if(state.canMakeMove(i)) {
					state.makeMove(i);
					moves[i] = -evaluate(state, i); // the recursive call
					//myID = rememberID;
					state.unMakeMove();
				}
			}
			// which column evaluated best
			for(int i = 0; i < 7; i++) {
				if(moves[i] > max) {
					max = moves[i];
					maxMove = i;
				}
			}
			// if we are back at level 1 then set chosenMove to the best column
			if(depth == 1) {
				chosenMove = maxMove;
				System.out.print(cap);
				for(int i = 0; i < 7; i++) {
					System.out.print("["+moves[i]+"]");
				}
				System.out.print("\n");
			}
			//System.out.println("Player "+myID+" Depth: "+depth+" minimum");
			depth--;
			//System.out.println(moves[minMove]);
			return moves[maxMove];
		}

		// for(int i = 0; i < 7; i++) {
		// 	System.out.print("["+state.getHeightAt(i)+"]");
		// }
		// System.out.println(
		// 	"Player: " + myID + " Depth: " + depth + " MyTwos:" + my2s + " MyThrees:" + my3s + " eTwos:" + e2s + " eThrees:" + e3s
		// );
		//System.out.println("Player "+myID+" Depth: "+depth+" evaluate");

		//System.out.println("OUT Depth:"+depth+" Player:"+myID+" Child:"+child);
		depth--;
		return (my2s*TWOFER + my3s*THREEFER);// - (e2s*TWOFER + e3s*THREEFER);
	}
}