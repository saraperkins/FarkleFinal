package edu.up.cs301.farkle;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;

/**
 * represents a computer player with a smart AI, designed to detect and select high scoring
 * combinations
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 22 March 2016
 */
public class FarkleSmartComputerPlayer extends GameComputerPlayer implements FarklePlayer, Tickable {
    /* ---=== Instance Variables ===---*/
    private FarkleState state;
    private String highestCombo;
    private int highestScore;
    private String[] diceSelections = new String[64];
    private ArrayList<GameAction> myCurActionList;
    private boolean diceChosen;
    
    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public FarkleSmartComputerPlayer(String name) {
        super(name);
        //set up the dice selections
        for (int i = 0; i < 64; i ++) {
            diceSelections[i] = Integer.toBinaryString(i);
            while (diceSelections[i].length()<6) {
                diceSelections[i] = "0"+diceSelections[i];
            }
        }
        highestScore = 0;
        highestCombo = null;
        myCurActionList = new ArrayList<GameAction>();
    }

    /**
     * recieve game info and make move if it is the computer's turn
     * @param info game info to be interpreted if it is a FarkleState
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof FarkleState) {
            state = (FarkleState) info;

            // look for farkle
            int diceInPlay = 0;
            int diceSelected = 0;
            for (Die d : state.getDice()) {
                if (d.isInPlay()) {
                    diceInPlay++;
                    if (d.isSelected()) {
                        diceSelected++;
                    }

                }
            }
            if (diceInPlay > 0 && state.hasFarkle()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException iex) {
                }

                if (state.getCurrentPlayer() == playerNum) {
                    game.sendAction(new FarkleAction(this));
                }
                return;
            }

            if (((FarkleState) info).getCurrentPlayer() != this.playerNum) {
                myCurActionList.clear();
                highestScore = 0;
                highestCombo = null;
                diceChosen = false;
                return;
            }

            // queue up the actions
            if(myCurActionList.size() == 0) {

                if (diceInPlay == diceSelected) {
                    myCurActionList.add(new RollAction(this));
                }
                else if (diceInPlay != 0 && (state.getRunningTotal() > 1000 || diceInPlay <= 3)) {
                    myCurActionList.add(new BankPointsAction(this));
                }
                else if (diceChosen) {
                    myCurActionList.add(new RollAction(this));
                }

                int dieOutOfPlay = 0;
                for (Die curDie: state.getDice()) {
                    if (!curDie.isInPlay()) {
                        dieOutOfPlay++;
                    }
                }
                if (dieOutOfPlay == 6) {
                    myCurActionList.add(new RollAction(this));
                } else {
                    chooseDice();
                    Log.i("my dice", highestCombo);
                    for (int i = 0; i < 6; i++) {
                        if (highestCombo.charAt(i) == '1') {
                            myCurActionList.add(new SelectDieAction(this, i));
                        }
                    }
                    highestCombo = null;
                    highestScore = 0;
                    diceChosen = true;
                }
            }
            if(myCurActionList.size() > 0) {
                GameAction curAction = myCurActionList.get(0);
                myCurActionList.remove(0);
                int sleepTime = 0;
                if (curAction instanceof RollAction) {
                    sleepTime = 300;
                    diceChosen = false;
                    myCurActionList.clear();
                    Log.i("computer", "rolling");
                }  else if (curAction instanceof SelectDieAction) {
                    sleepTime = 300;
                    //Log.i(""+((SelectDieAction)(curAction)).getIdxOfDie(), "selected");
                } else if (curAction instanceof BankPointsAction) {
                    sleepTime = 800;
                    diceChosen = false;
                    Log.i("banking", "points");
                }
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    //nothing
                }
                this.game.sendAction(curAction);
            }

        }
    }

    /**
     * save the highest combo and its score to be used in making a move
     * @return true if new combo is picked
     */
    public boolean chooseDice() {
        Log.i("choosing", "dice");
        for (String currSel : diceSelections) {
            for (int i = 0; i < 6; i++) {
                if (currSel.charAt(i) == '0') {
                    if (state.getDice()[i].isSelected()) {
                        state.selectDie(i);
                    }
                } else {
                    if (!state.getDice()[i].isInPlay()) {
                        break;
                    }
                    if (!state.getDice()[i].isSelected()) {
                        state.selectDie(i);
                    }
                }
            }

            if (state.getRunningTotal() > highestScore) {
                highestScore = state.getRunningTotal();
                highestCombo = currSel;

            }
        }
        return true;
    }

    /**
     * callback method: the timer ticked
     */
    protected void timerTicked() {

    }

    /**
     * decide whether to bank or roll
     * @return true if bank is the best
     */
    public boolean bankCurSelected () {
        return true;
    }
}