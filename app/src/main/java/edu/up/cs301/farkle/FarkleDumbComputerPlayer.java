package edu.up.cs301.farkle;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.game.Game;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;

/**
 * represents a computer player with a dumb AI
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 12 April 2016
 */
public class FarkleDumbComputerPlayer extends GameComputerPlayer implements FarklePlayer, Tickable {
    /* ---=== Instance Variables ===---*/
    private FarkleState state;
    private String validCombo; // score-able dice combo -- binary string
    private int validScore;
    private boolean diceChosen;
    
    // additions
    private String[] diceSelections = new String[64];
    private ArrayList<GameAction> myCurActionList;
    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public FarkleDumbComputerPlayer(String name) {
        super(name);
        //set up the dice selections
        for (int i = 0; i < 64; i ++) {
            diceSelections[i] = Integer.toBinaryString(i);
            while (diceSelections[i].length()<6) {
                diceSelections[i] = "0"+diceSelections[i];
            }
        }
        validScore = 0;
        validCombo = null;
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
            boolean diceInPlay = false;
            for (Die d : state.getDice()) {
                if (d.isInPlay()) {
                    diceInPlay = true;
                }
            }
            if (diceInPlay && state.hasFarkle()) {
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
                validScore = 0;
                validCombo = null;
                diceChosen = false;
                return;
            }

            // queue up the actions
            if(myCurActionList.size() == 0) {
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
                    Log.i("my dice", validCombo);
                    for (int i = 0; i < 6; i++) {
                        if (validCombo.charAt(i) == '1') {
                            myCurActionList.add(new SelectDieAction(this, i));
                        }
                    }
                    validCombo = null;
                    validScore = 0;
                    diceChosen = false;
                    myCurActionList.add(new BankPointsAction(this));
                }
            }
            if(myCurActionList.size() > 0) {
                GameAction curAction = myCurActionList.get(0);
                myCurActionList.remove(0);
                int sleepTime = 0;
                if (curAction instanceof RollAction) {
                    sleepTime = 600;
                    Log.i("computer", "rolling");
                }  else if (curAction instanceof SelectDieAction) {
                    sleepTime = 500;
                    Log.i(""+((SelectDieAction)(curAction)).getIdxOfDie(), "selected");
                } else if (curAction instanceof BankPointsAction) {
                    sleepTime = 1200;
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
     * save a valid combo and valid score to be used in making a move
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
            if (state.getRunningTotal() > validScore) {
                validScore = state.getRunningTotal();
                validCombo = currSel;
                diceChosen = true;
                Log.i("dice", "chosen");
                return true;
            }
        }
        return true;
    }
    
    /**
     * callback method: the timer ticked
     */
    protected void timerTicked() {
        
    }
}
