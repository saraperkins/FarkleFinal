package edu.up.cs301.farkle;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.up.cs301.game.Game;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * represents a human player in the game, allowing the person to interact with the
 * gui in order to play
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 12 April 2016
 */
public class FarkleAIwithGUI extends GameHumanPlayer {
    /* ---=== Instance Variables ===--- */
    // text views
    protected TextView p0scoreText, p1scoreText, runningTotalText, playerOneText, playerTwoText;
    // image views
    protected static ImageView playerOneImage, playerTwoImage, farkleImage1, farkleImage2;

    // buttons
    protected Button rollDiceButton, bankPointsButton;
    protected ImageButton[] diceButtons = new ImageButton[6];

    private FarkleState state;
    private String highestCombo;
    private int highestScore;
    private String[] diceSelections = new String[64];
    private ArrayList<GameAction> myCurActionList;
    private boolean diceChosen;

    //images
    //    protected int[] picId = {R.drawable.avatar_girl, R.drawable.avatar_boy1,R.drawable.avatar_boy2,
    //                             R.drawable.avatar_puppy};

    private int diceStyle = 0;
    // 0 = red; 1 = pink; 2 = sunset; 3 = purple; 4 = nux

    private int lastFarkleId = -1;

    // image res id's
    protected int[] diceWhiteResID = {R.drawable.white_one_die,R.drawable.white_two_die,
            R.drawable.white_three_die, R.drawable.white_four_die,
            R.drawable.white_five_die,R.drawable.white_six_die};
    protected int[] imageButtonId = {R.id.dieOne, R.id.dieTwo, R.id.dieThree, R.id.dieFour,
            R.id.dieFive, R.id.dieSix};

    protected int[][] diceResID = {{R.drawable.red_one_die,R.drawable.red_two_die,
            R.drawable.red_three_die, R.drawable.red_four_die,
            R.drawable.red_five_die,R.drawable.red_six_die},{ R.drawable.pink_one_die,R.drawable.pink_two_die,
            R.drawable.pink_three_die, R.drawable.pink_four_die,
            R.drawable.pink_five_die,R.drawable.pink_six_die},{ R.drawable.sunset_one_die,R.drawable.sunset_two_die,
            R.drawable.sunset_three_die, R.drawable.sunset_four_die,
            R.drawable.sunset_five_die,R.drawable.sunset_six_die},{R.drawable.purple_one_die,R.drawable.purple_two_die,
            R.drawable.purple_three_die, R.drawable.purple_four_die,
            R.drawable.purple_five_die,R.drawable.purple_six_die},{R.drawable.nux_one_die,R.drawable.nux_two_die,
            R.drawable.nux_three_die, R.drawable.nux_four_die,
            R.drawable.nux_five_die,R.drawable.nux_six_die},{R.drawable.veg_one_die,R.drawable.veg_two_die,
            R.drawable.veg_three_die, R.drawable.veg_four_die,
            R.drawable.veg_five_die,R.drawable.veg_six_die},{R.drawable.black_one_die,R.drawable.black_two_die,
            R.drawable.black_three_die, R.drawable.black_four_die,
            R.drawable.black_five_die,R.drawable.black_six_die}};

    // game play variables
    private GameMainActivity myActivity;

    private boolean flashFarkle;
    /**
     * constructor for a human player
     *
     *
     * @param name name of the player
     */
    public FarkleAIwithGUI(String name) {
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
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view hierarchy
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message -- to be used if a game state
     */
    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof FarkleState) {
            state = (FarkleState) info;
            updateDisplay();

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
                farkleImage1.setVisibility(View.VISIBLE);
                farkleImage2.setVisibility(View.VISIBLE);
                farkleImage1.invalidate();
                farkleImage2.invalidate();
                updateDisplay();
                this.lastFarkleId = state.getCurrentPlayer();
                getTimer().setInterval(1500);
                getTimer().start();
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
                    sleepTime = 400;
                    diceChosen = false;
                    myCurActionList.clear();
                }  else if (curAction instanceof SelectDieAction) {
                    sleepTime = 300;
                } else if (curAction instanceof BankPointsAction) {
                    sleepTime = 1000;
                    diceChosen = false;
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
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     * sets all of the view variables
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity) {

        myActivity = activity;
        activity.setContentView(R.layout.farkle_human_player);

        if ( activity instanceof FarkleMainActivity) {
            ((FarkleMainActivity)activity).setGuiPlayer(this);
        }

        ActionBar actionBar = activity.getActionBar();
        actionBar.show();

        // text views
        p0scoreText = (TextView)activity.findViewById(R.id.p0CurrentScore);
        p1scoreText = (TextView)activity.findViewById(R.id.p1CurrentScore);
        runningTotalText = (TextView)activity.findViewById(R.id.bankScore);
        playerOneText = (TextView)activity.findViewById(R.id.playerOneText);
        playerTwoText = (TextView)activity.findViewById(R.id.playerTwoText);

        // image views
        playerOneImage = (ImageView)activity.findViewById(R.id.playerOneImage);
        playerTwoImage = (ImageView)activity.findViewById(R.id.playerTwoImage);
        farkleImage1 = (ImageView)activity.findViewById(R.id.farkleTextImageView1);
        farkleImage2 = (ImageView)activity.findViewById(R.id.farkleTextImageView2);

        playerOneImage.setImageResource(R.drawable.avatar_girl);
        playerTwoImage.setImageResource(R.drawable.avatar_puppy);

        // buttons
        rollDiceButton = (Button)activity.findViewById(R.id.rollDiceButton);
        bankPointsButton = (Button)activity.findViewById(R.id.bankPointsButton);
        ImageButton dieOne = (ImageButton)activity.findViewById(R.id.dieOne);
        ImageButton dieTwo = (ImageButton)activity.findViewById(R.id.dieTwo);
        ImageButton dieThree = (ImageButton)activity.findViewById(R.id.dieThree);
        ImageButton dieFour = (ImageButton)activity.findViewById(R.id.dieFour);
        ImageButton dieFive = (ImageButton)activity.findViewById(R.id.dieFive);
        ImageButton dieSix = (ImageButton)activity.findViewById(R.id.dieSix);
        diceButtons[0] = dieOne;  diceButtons[1] = dieTwo;  diceButtons[2] = dieThree;
        diceButtons[3] = dieFour; diceButtons[4] = dieFive; diceButtons[5] = dieSix;


        farkleImage1.setVisibility(View.INVISIBLE);
        farkleImage2.setVisibility(View.INVISIBLE);

    }



    /**
     * display the die using the image id's
     */
    public void displayDie() {
        for(int i=0; i<6;i++) {
            Die curDie = state.getDice()[i];
            if(!curDie.isInPlay()) {
                diceButtons[i].setVisibility(View.INVISIBLE);
            }
            else {
                diceButtons[i].setVisibility(View.VISIBLE);
                if(curDie.isSelected()) {
                    diceButtons[i].setImageResource(diceWhiteResID[curDie.getValue()-1]);
                }
                else {
                    if (state.getCurrentPlayer() == playerNum) {
                        diceButtons[i].setImageResource(diceResID[diceStyle][curDie.getValue() - 1]);
                    } else {
                        diceButtons[i].setImageResource(diceResID[diceResID.length-1][curDie.getValue() - 1]); // change to the generic
                    }
                }
            }
            diceButtons[i].invalidate();
        }
    }

    /**
     * set the display based on the current state
     */
    protected void updateDisplay() {
        farkleImage1.invalidate();
        farkleImage2.invalidate();

        //update dice
        displayDie();

        //indicate whose turn it is by highlighting their name
        if (state.getCurrentPlayer() == 0) {//player 1's turn
            playerOneText.setTextColor(Color.BLACK);
            playerOneText.setBackgroundColor(Color.WHITE);
            playerTwoText.setTextColor(Color.WHITE);
            playerTwoText.setBackgroundColor(0x00000000);
        }
        else{ //player 2's turn
            playerTwoText.setTextColor(Color.BLACK);
            playerTwoText.setBackgroundColor(Color.WHITE);
            playerOneText.setTextColor(Color.WHITE);
            playerOneText.setBackgroundColor(0x00000000);
        }


        //set scores & running total
        p0scoreText.setText(state.getPlayerScores()[0] + "");
        p1scoreText.setText(state.getPlayerScores()[1] + "");
        runningTotalText.setText(state.getRunningTotal() + "");
        playerOneText.setText(this.allPlayerNames[0]);
        playerTwoText.setText(this.allPlayerNames[1]);
    }

    /**
     * perform farkle signal at timer ticked
     */
    @Override
    public void timerTicked() {
        /**
         * External Citation:
         * Date:    12 April 2016
         * Problem:  making the farkle signal show
         * Resource: Nux
         * Solution: Use use a timer and invalidate the views
         */
        farkleImage1.setVisibility(View.INVISIBLE);
        farkleImage2.setVisibility(View.INVISIBLE);
        farkleImage1.invalidate();
        farkleImage2.invalidate();
        if (this.lastFarkleId == playerNum) {
            game.sendAction(new FarkleAction(this));
        }
        this.lastFarkleId = -1;
        getTimer().stop();
    }

    /**
     * setDiceStyle - sets the dice style according to user selection from menu
     * @param diceStyle which style to change to
     */
    public void setDiceStyle(int diceStyle) {
        if (diceStyle >= 0 && diceStyle < 6) {
            this.diceStyle = diceStyle;
            displayDie();
        }
    }


    /**
     * save the highest combo and its score to be used in making a move
     * @return true if new combo is picked
     */
    public boolean chooseDice() {
        highestCombo = diceSelections[1];
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
}

