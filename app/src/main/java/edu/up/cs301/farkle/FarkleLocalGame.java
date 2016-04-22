package edu.up.cs301.farkle;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import android.util.Log;

/**
 * This contains the state of the local game for the Farkle game.
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public class FarkleLocalGame extends LocalGame implements FarkleGame {
	private FarkleState myFarkleGameState;

	public FarkleLocalGame () {
		myFarkleGameState = new FarkleState();
	}

	/**
	 * send an updated version of the state to a given player
	 * @param p which player to send the state to
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		p.sendInfo(new FarkleState(myFarkleGameState));
	}


	/**
	 * can the player with the given id take an action right now?
	 * @param playerIdx index of the player attempting to move
	 */
	@Override
	protected boolean canMove(int playerIdx) {
		return (myFarkleGameState.getCurrentPlayer() == playerIdx);
	}

	/**
	 * check the scores to see if the game has ended
	 * @return true if it is over
	 */
	@Override
	protected String checkIfGameOver() {
		if (myFarkleGameState.getPlayerScores()[0] >= 10000) {
			return this.playerNames[0] + " won with " + myFarkleGameState.getPlayerScores()[0] + " points! Yaaaaaay!";
		} else if (myFarkleGameState.getPlayerScores()[1] >= 10000) {
			return this.playerNames[1] + " won with " + myFarkleGameState.getPlayerScores()[1] + " points! Yaaaaaay!";
		}
		return null;
	}

	/**
	 * This method is called when a new action arrives from a player
	 *
	 * @param action the action that was sent by the player, either RollDiceAction or
	 *               BankPointsAction or SelectDieAction
	 *
	 * @return true if the action was taken or false if the action was invalid/illegal.
	 */
	@Override
	protected boolean makeMove(GameAction action) {
		if (action instanceof FarkleAction) {
			myFarkleGameState.farkle();
			return true;
		} else if (action instanceof RollAction) {
			myFarkleGameState.rollDice();
			return true;
		} else if (action instanceof BankPointsAction) {
			myFarkleGameState.bankPoints();
			return true;
		} else if (action instanceof SelectDieAction) {
			myFarkleGameState.selectDie(((SelectDieAction) action).getIdxOfDie());
            Log.i("Idx: ",((SelectDieAction) action).getIdxOfDie()+"" );
			return true;
		} else {
			return false;
		}

	}
}// class FarkleLocalGame
