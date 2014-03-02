import java.util.Random;
import java.util.ArrayList;
import java.util.Stack;

/*
*	So after working on minimax_bs for too long w/o success I translated a
*	python implementation into java and this is the result of that.
*/

public class minimax_py extends AIModule
{
	private static final int THREEFER = 100; // weight for 3-in-a-row's
	private static final int TWOFER = 1; // weight for 2-in-a-row's
	private int counta; // debugging counter

	public void getNextMove(final GameStateModule state) {
		int myID = state.getActivePlayer();
		int eID = myID == 1 ? 2 : 1;
		int depth = 3; // how many plies deep in the game tree we look
		counta = 0;
		int[] legalMoves = new int[7];
		Stack<Integer> stack = new Stack<Integer>(); // this stack just for debugging
		stack.push(depth);
		// foreach column create a unique copy of the gamestate and play that column
		// then call the recursive evaluate function to populate the legalMoves array
		// with "goodness" numbers for each move
		for(int i = 0; i < 7; i++) {
			if(state.canMakeMove(i)) {
				GameStateModule copy = state.copy();
				copy.makeMove(i);
				legalMoves[i] = evaluate(depth, copy, eID, stack);
			}
			else {
				legalMoves[i] = Integer.MIN_VALUE; // don't pick full columns
			}
		}
		int max = -Integer.MAX_VALUE;
		int best = -1;
		// find the column with the maximum "goodness"
		for(int i = 0; i < 7; i++) {
			System.out.print("["+legalMoves[i]+"]");
			if(legalMoves[i] != Integer.MIN_VALUE) {
				if(legalMoves[i] > max) {
					max = legalMoves[i];
					best = i;
				}
			}
		}
		chosenMove = best; // choose it
	}

	// recursive function that calls value() on each state at the chosen depth
	private int evaluate(final int depth, final GameStateModule state, final int myID, Stack<Integer> stack) {
		// helpful debugging prints
		System.out.print(counta);
		for(int i = 0; i < stack.size(); i++) {
			System.out.print("("+stack.get(i)+")");
		}
		int eID = myID == 1 ? 2 : 1;
		// populate array of gamestate copies differentiated by the column chosen
		ArrayList<GameStateModule> legalMoves = new ArrayList<GameStateModule>();
		for(int i = 0; i < 7; i++) {
			if(state.canMakeMove(i)) {
				GameStateModule temp = state.copy();
				temp.makeMove(i);
				legalMoves.add(temp);
			}
		}
		// under 3 circumstances do we stop recursing
		if(depth == 0 || state.getCoins() == 42 || terminate) {
			int x = value(myID, state); // here's the call to value()
			System.out.println("["+x+"]"); // debugging
			printState(state); // debugging
			return x;
		}
		
		int max = -Integer.MAX_VALUE;
		for(int i = 0; i < legalMoves.size(); i++) {
			counta++;
			stack.push(depth-1);
			// notice the negative sign on the recursive call
			max = Math.max(max, -evaluate(depth-1, legalMoves.get(i), eID, stack));
			stack.pop();
			System.out.print("\n");
		}
		return max;
	}

	// assigns a "goodness" value to a given gamestate from the perspective of the given player
	private int value(final int myID, final GameStateModule state) {
		int eID = myID == 1 ? 2 : 1;
		int my2s = 0;
		int my3s = 0;
		int e2s = 0;
		int e3s = 0;
		int myCount = 0;
		int eCount = 0;
		
		// this loop finds horizontal potential victories
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
					//System.out.print("Loss ");
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
					//System.out.print("Win ");
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// this loop finds positive slope diagonal potential victories
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
					//System.out.print("Loss ");
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
					//System.out.print("Win ");
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// this loop finds negative slope diagonal potential victories
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
					//System.out.print("Loss ");
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
					//System.out.print("Win ");
						return Integer.MAX_VALUE;
					}
				}
			}
		}

		// this loop finds vertical potential victories
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
					//System.out.print("Win ");
					return Integer.MAX_VALUE;
				}
				else {
					//System.out.print("Loss ");
					return -Integer.MAX_VALUE;
				}
			}
		}
		// the example doesn't subtract the opponents twos and threes
		// i've tried both ways
		int ret = (my2s*TWOFER + my3s*THREEFER) - (e2s*TWOFER + e3s*THREEFER);
		//System.out.print(ret+" ");
		return ret;
	}

	// debugging function. prints a board state
	private void printState(final GameStateModule state) {
		for(int j = 5; j >= 0; j--) {
			for(int i = 0; i < 7; i++) {
				System.out.print("["+state.getAt(i,j)+"]");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}
}