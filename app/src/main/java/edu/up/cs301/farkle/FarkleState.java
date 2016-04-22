package edu.up.cs301.farkle;

import android.util.Log;

import edu.up.cs301.game.infoMsg.GameState;
/**
 * External Citation:
 * Date: 	 15 March 2016
 * Problem:  getting android to identify R
 * Resource: Nux
 * Solution: Use the counter project as a base, then replace all counter items with farkle
 */


/**
 * This contains the state for the Farkle game.
 * 
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 10 April 2016
 */
public class FarkleState extends GameState {
	
	// to satisfy Serializable interface
	private static final long serialVersionUID = 7737393762469851826L;

    /* ---=== Instance Variables ===--- */
	private int runningTotal;
	private int[] playerScores;
	private int currentPlayer;
	private Die[] dice;
	private int preselectRunningTotal;
	private int[] count = {0,0,0,0,0,0}; // for trivial math purposes, not to be copied

	/* ---=== Constructors ===--- */

	/**
	 * constructor for the farkle game state
	 */
	public FarkleState () {
		runningTotal = 0;
		currentPlayer = 0;
		playerScores = new int[2];
		for(int i = 0; i < playerScores.length; i++) {
			playerScores[i] = 0;
		}
		dice = new Die[6];
		for (int i = 0; i < dice.length; i++) {
			dice[i] = new Die();
		}
		preselectRunningTotal = 0;
	}

	/**
	 * constructor for the farkle game state, returns a deep copy
	 * @param cpy FarkleSate to make the copy of
	 */
	public FarkleState (FarkleState cpy) {
		runningTotal = cpy.getRunningTotal();
		playerScores = new int[2];
		for (int i = 0; i < playerScores.length; i++) {
			playerScores[i] = cpy.playerScores[i];
		}
		currentPlayer = cpy.getCurrentPlayer();
		dice = new Die[6];
		for (int i = 0; i < dice.length; i++) {
			dice[i] = new Die(cpy.getDice()[i]);
		}
		preselectRunningTotal = cpy.getPreselectRunningTotal();
	}

	/* ---=== GamePlay Methods ===--- */

	/**
	 * determine whether die have been selected
	 * @return true is no die have been selected
	 */
	public boolean noDieSelectedInCurrentTurn() {
		boolean noneSelected = true;
		for (Die die: dice) {
			if (die.isInPlay() && die.isSelected()) {
				noneSelected = false;
				break;
			}
		}
		return noneSelected;
	}

	/**
	 * reset and roll if all dice are out of play
	 * @return true if roll was successful
	 */
	private boolean rollDiceIfOutOfPlay() {
		int dieOutOfPlay = 0;
		for (Die die: dice) {
			if(!die.isInPlay()) {
				dieOutOfPlay ++;
			}
		}
		if (dieOutOfPlay == 6) {
			for (Die die: dice) {
				die.reset();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * roll all the dice in play if the player is allowed
	 * @return true if the roll was successful
	 */
	public boolean rollDice() {
		/*****************/
		boolean diceInPlay = false;
		for (Die d : dice) {
			if (d.isInPlay()) {
				diceInPlay = true;
			}
		}
		if (diceInPlay && hasFarkle()) { /****************/
			farkle();
		}
		// all die are out of play -- means curPlayer can roll bc player has just changed
		if (rollDiceIfOutOfPlay()) {
			if (hasFarkle()) { runningTotal = 0;}
            preselectRunningTotal = runningTotal; // added to fix scoring bug with rolling all 6 die -- should not be a problem, should be 0 anyway
			return true;
		}

		// determine if player is not allowed to bank points
		// need to protect rolling -- something must be selected
		boolean noneSelected = noDieSelectedInCurrentTurn();
		if (noneSelected) { return false; }
		if (runningTotal == preselectRunningTotal) { return false; } // this will cover invalid selections

		// first remove die if not in play, then standard roll
		for (Die die: dice) {
			if (die.isSelected()) {
				die.setIsInPlay(false);
			}
			if (die.isInPlay()) {
				die.roll();
			}
		}

		// all die are out of play -- after selection occurs
		if (rollDiceIfOutOfPlay()) {
			if (hasFarkle()) { runningTotal = 0;}
            preselectRunningTotal = runningTotal; // added to fix scoring bug with rolling all 6 die
			return true;
		}

		if (hasFarkle()) { runningTotal = 0; }

		preselectRunningTotal = runningTotal; // reset on action
		return true;
	}

	/**
	 * copy a length 6 array
	 * @param cpy array to copy
	 * @param dest array destination
	 */
	public void cpy6intarray(int[] cpy, int[] dest) {
		for (int i = 0; i < 6; i++) {
			dest[i] = cpy[i];
		}
	}

	/**
	 * select a die object and recalculate the running total accordingly
	 * @param idx index of selected die object
	 */
	public void selectDie(int idx) {
		boolean diceInPlay = false;
		for (Die d : dice) {
			if (d.isInPlay()) {
				diceInPlay = true;
			}
		}
		if (diceInPlay && hasFarkle()) {
			farkle();
		}
		dice[idx].setIsSelected(!dice[idx].isSelected());
		if (noDieSelectedInCurrentTurn()) {
			runningTotal = preselectRunningTotal;
			return;
		}
		// recalculate point options, set running total to add selected points
		runningTotal = preselectRunningTotal;
		updateCount(false);
		int[] countBackup = new int[6];
		cpy6intarray(count, countBackup);

		if (sixOfAKind(false)) { // 3000 six of a kind
			runningTotal += FarkleScorer.SIX_OFAKIND; return; // six die
		}
		if (twoTriplets(false)) { // 2500 two triplets
			runningTotal += FarkleScorer.TWO_TRIPLES; return; // six die
		}
		if (fiveOfAKind(false)) { // 2000 five of a kind
			runningTotal += FarkleScorer.FIVE_OFAKIND;
			for (int i = 0; i < 6; i++) { if (count[i] == 5) { count[i] = 0; } }
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (fourAndPair(false)) { // 1500 four of a number and a pair
			runningTotal += FarkleScorer.FULL_HOUSE; return; // six die
		}
		if (threePairs(false)) { // 1500 three pairs
			runningTotal += FarkleScorer.THREE_PAIR; return; // six die
		}
		if (straight(false)) { // 1500 straight 1-6
			runningTotal += FarkleScorer.STRAIGHT; return; // six die
		}
		if (fourOfAKind(false)) { // 1000 four of a kind
			runningTotal += FarkleScorer.FOUR_OFAKIND;
			for (int i = 0; i < 6; i++) { if (count[i] == 4) { count[i] = 0; } }
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count);}
		if (threeOfAKind(false) == 6) { // 600 triple six
			runningTotal += FarkleScorer.tripleScore(6);
			count[6-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (threeOfAKind(false) == 5) { // 500 triple five
			runningTotal += FarkleScorer.tripleScore(5);
			count[5-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (threeOfAKind(false) == 4) { // 400 triple four
			runningTotal += FarkleScorer.tripleScore(4);
			count[4-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (threeOfAKind(false) == 3) { // 300 triple three
			runningTotal += FarkleScorer.tripleScore(3);
			count[3-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (threeOfAKind(false) == 2) { // 200 triple two
			runningTotal += FarkleScorer.tripleScore(2);
			count[2-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		if (threeOfAKind(false) == 1) { // 300 triple one
			runningTotal += FarkleScorer.tripleScore(1);
			count[1-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		// 100 single one
		int numOnes = numSingles(1, false);
		if (numOnes > 0) {
			runningTotal += numOnes * FarkleScorer.SINGLE_1;
		    count[1-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }
		// 50 single five
		int numFives = numSingles(5, false);
		if (numFives > 0) {
			runningTotal += numFives * FarkleScorer.SINGLE_5;
			count[5-1] = 0;
			cpy6intarray(count, countBackup);
		} else { cpy6intarray(countBackup, count); }

		// check for invalid selection -- set runningTotal to preSelectRunningTotal
		boolean dieLeftOver = false;
		for (int i = 0; i < 6; i++) {
			if (count[i] > 0) {
				runningTotal = preselectRunningTotal;
				return;
			}
		}
	}

	/**
	 * bank points if the player is allowed
	 * @return true if the bank was successful
	 */
	public boolean bankPoints() {
		boolean diceInPlay = false;
		for (Die d : dice) {
			if (d.isInPlay()) {
				diceInPlay = true;
			}
		}
		if (diceInPlay && hasFarkle()) {
			Log.i("i am here", "me");
			farkle();
		}
		// determine if player is not allowed to bank points
		// need to protect rolling -- something must be selected
		boolean noneSelected = noDieSelectedInCurrentTurn();
		if (noneSelected) { return false; }
		if (runningTotal == preselectRunningTotal) { return false; } // this will cover invalid selections

		// add the running total to the score of the current player
		playerScores[currentPlayer] += runningTotal;

				// switch players and reset
		farkle(); // not really a farkle, but use for reset functionality

		return true;
	}

	/**
	 * switch players and reset the settings
	 */
	public void farkle() {
		runningTotal = 0;
		/**
		 * External Citation:
		 * Date: 	 16 March 2016
		 * Problem:  find a way to toggle the player with minimal code
		 * Resource: http://stackoverflow.com/questions/4084050/can-you-use-arithmetic
		 * 			 -operators-to-flip-between-0-and-1
		 * Solution: use 0 and 1, then do x = 1 - x
		 */
		currentPlayer = 1 - currentPlayer;
		for (Die die: dice) {
			die.setIsInPlay(false); // take all die out of play -- fixed when new cur player rolls
		}
		preselectRunningTotal = 0;
	}


	/* ---=== Dice Combination Detection ===--- */

	/**
	 * check for a farkle
	 * @return true if the player has farkled
	 */
	public boolean hasFarkle(){
		return (!threePairs(true) &&
				threeOfAKind(true)== -1 &&
				!fourOfAKind(true) &&
				!fiveOfAKind(true) &&
				!sixOfAKind(true) &&
				!straight(true) &&
				!twoTriplets(true) &&
				!fourAndPair(true) &&
				numSingles(1, true) <= 0 &&
				numSingles(5, true) <= 0);
	}

	/**
	 * updateCount makes the count array all zeros
	 * and then updates the array with the current
	 * number of each die value
	 *
	 * @param farkleCheck are we checking for a farkle
	 */
	public void updateCount(boolean farkleCheck) {
		for(int i=0;i<6;i++) {
			count[i] = 0;
		}
		for(int i=0;i<6;i++) {

			if(dice[i].isInPlay()) {
				if (farkleCheck) {
					count[dice[i].getValue() - 1]++;
				} else if (dice[i].isSelected()) {
					count[dice[i].getValue() - 1]++;
				}
			}
		}
	}

	/**
	 * threeOfAKind() tests for a three of a kind
	 * @return -1 if no three of a kinds and the number
	 * that has a three of a kind if it exists
	 * @param farkleCheck are we checking for a farkle
	 */
	public int threeOfAKind(boolean farkleCheck){
		//check for single
		if (farkleCheck) updateCount(farkleCheck);
		if(checkNum(3, farkleCheck) == -1)
			return -1; //no three of a kinds are in play
		else
			return checkNum(3, farkleCheck);
	}

	/**
	 * check for a four of a kind
	 * @param farkleCheck are we checking for a farkle
	 * @return true if there is a four of a kind
	 */
	public boolean fourOfAKind(boolean farkleCheck){
		if (farkleCheck) updateCount(farkleCheck);
		return checkNum(4, farkleCheck) != -1;
	}

	/**
	 * check for a five of a kind
	 * @param farkleCheck are we checking for a farkle
	 * @return true if there is a five of a kind
	 */
	public boolean fiveOfAKind(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		return checkNum(5, farkleCheck) != -1;
	}

	/**
	 * check for a six of a kind
	 * @param farkleCheck are we checking for a farkle
	 * @return true if there is a six of a kind
	 */
	public boolean sixOfAKind(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		return checkNum(6, farkleCheck) != -1;
	}

	/**
	 * checkNum checks to see if the die is selected if
	 * the ____ of a kind exists.
	 * @param kind which "____ of a kind" we want to check
	 * @param farkleCheck are we checking for a farkle
	 * @return -1 for false and the value number with the
	 * proper ____ of a kind
	 */
	public int checkNum(int kind, boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		for (int i = 0; i < 6; i++) {
			if (count[i] == kind) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * straight searches to see if a straight exists
	 * @param farkleCheck are we checking for a farkle
	 * @return true if a straight exists
	 */
	public boolean straight(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		for (int j=0; j<6;j++) {
			if (count[j] != 1)
				return false;
		}
		return true;
	}

	/**
	 * threePairs - checks to see if three pairs are present
	 * @param farkleCheck are we checking for a farkle
	 * @return true if three pairs are present
	 */
	public boolean threePairs(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		int numPairs = 0;
		for (int j=0; j<6;j++) {
			if (count[j] ==2)
				numPairs++;
		}
		return numPairs==3;
	}

	/**
	 * twoPairs  - checks to see if two triplets exist
	 * @param farkleCheck are we checking for a farkle
	 * @return true if two triplets exist
	 */
	public boolean twoTriplets(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		int numTriplets = 0;
		for (int j=0; j<6;j++) {
			if (count[j] ==3)
				numTriplets++;
		}
		return numTriplets==2;
	}

	/**
	 * fourAndPair - looks to see if four of a kind
	 * and two of a kind exist
	 * @param farkleCheck are we checking for a farkle
	 * @return true if four and a pair exists
	 */
	public boolean fourAndPair(boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		boolean isFour = false;
		boolean isTwo = false;
		for (int j=0; j<6;j++) {
			if (count[j] ==4)
				isFour = true;
			if (count[j] == 2)
				isTwo = true;
		}
		return (isFour && isTwo);
	}

	/**
	 * numSingles - finds the number of a single die
	 * value and returns that count
	 * @param val value on die that you want to know
	 *            how many single exist
	 * @param farkleCheck are we checking for a farkle
	 * @return number of instances of val in current dice array
	 */
	public int numSingles(int val, boolean farkleCheck) {
		if (farkleCheck) updateCount(farkleCheck);
		return count[val-1];
	}


	/* ---=== Accessors ===--- */
	public int getRunningTotal() {
		return runningTotal;
	}

	public int[] getPlayerScores() {
		return playerScores;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public Die[] getDice() { return dice; }

	public int getPreselectRunningTotal() { return preselectRunningTotal; }

	public int[] getCount() { return count; }

	/* ---=== Setters ===--- */
	public void setRunningTotal(int runningTotal) {
		this.runningTotal = runningTotal;
	}

	public void setPlayerScores(int playerIdx, int newScoreValue) {
		if ( playerIdx < playerScores.length && playerIdx >= 0) {
			playerScores[playerIdx] = newScoreValue;
		}
	}

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}


}
