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

public class Minimax extends AIModule
{
	private int[] moves;

	public void getNextMove(final GameStateModule state){
		//this is an array the width of the board that symbolizes each possible move
		moves = new int[state.getWidth()];


	}
}
