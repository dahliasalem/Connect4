import java.util.Random;
import java.util.HashMap;
import java.util.ArrayList;

public class minimax_py extends AIModule
{
	private final Random r = new Random(System.currentTimeMillis());
	private static final int THREEFER = 100;
	private static final int TWOFER = 1;
	
	public minimax_py() {
		
	}

	public void getNextMove(final GameStateModule state) {
		int myID = state.getActivePlayer();
		int eID = myID == 1 ? 2 : 1;
		int depth = 8;
		int[] legalMoves = new int[7];
		for(int i = 0; i < 7; i++) {
			if(state.canMakeMove(i)) {
				GameStateModule copy = state.copy();
				copy.makeMove(i);
				legalMoves[i] = evaluate(depth, copy, eID);
			}
			else {
				legalMoves[i] = Integer.MIN_VALUE;
			}
		}
		int max = -Integer.MAX_VALUE;
		int best = -1;
		for(int i = 0; i < 7; i++) {
			System.out.print("["+legalMoves[i]+"]");
			if(legalMoves[i] != Integer.MIN_VALUE) {
				if(legalMoves[i] > max) {
					max = legalMoves[i];
					best = i;
				}
			}
		}
		chosenMove = best;
	}

	private int evaluate(final int depth, final GameStateModule state, final int myID) {
		
		int eID = myID == 1 ? 2 : 1;
		ArrayList<GameStateModule> legalMoves = new ArrayList<GameStateModule>();
		for(int i = 0; i < 7; i++) {
			if(state.canMakeMove(i)) {
				GameStateModule temp = state.copy();
				temp.makeMove(i);
				legalMoves.add(temp);
			}
		}
		if(depth == 0 || state.getCoins() == 42 || terminate) {
			return value(myID, state);
		}
		
		int max = -Integer.MAX_VALUE;
		for(int i = 0; i < legalMoves.size(); i++) {
			max = Math.max(max, -evaluate(depth-1, legalMoves.get(i), eID));
		}
		return max;
	}

	private int value(final int myID, final GameStateModule state) {
		int eID = myID == 1 ? 2 : 1;
		int my2s = 0;
		int my3s = 0;
		int e2s = 0;
		int e3s = 0;
		int myCount = 0;
		int eCount = 0;
		
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
		int ret = my2s*TWOFER + my3s*THREEFER;
		//System.out.print(ret+" ");
		return ret;
	}

	private boolean isLegal(final int x, final int y) {
		return y >= 0 && y <= 5 && x >= 0 && x <= 6;
	}
}