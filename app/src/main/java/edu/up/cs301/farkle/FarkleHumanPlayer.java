package edu.up.cs301.farkle;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
public class FarkleHumanPlayer extends GameHumanPlayer implements View.OnClickListener {
    /* ---=== Instance Variables ===--- */
    // text views
    protected TextView p0scoreText, p1scoreText, runningTotalText, playerOneText, playerTwoText;
    // image views
    protected static ImageView playerOneImage, playerTwoImage, farkleImage1, farkleImage2;
    
    // buttons
    protected Button rollDiceButton, bankPointsButton;
    protected ImageButton[] diceButtons = new ImageButton[6];
    
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
    private FarkleState myState;

    private boolean flashFarkle;
    /**
     * constructor for a human player
     *
     *
     * @param name name of the player
     */
    public FarkleHumanPlayer(String name) {
        super(name);
        
        
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
        // make sure the background is currently black -- not stuck on a flash
        View top = this.getTopView();
        if (top == null) return;
        top.setBackgroundColor(0xFF000000);

        // ignore the message if it's not a FarkleState message
        if (!(info instanceof FarkleState)) {
            //if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            if (info instanceof NotYourTurnInfo) {
                this.flash(0xff49a17f, 100);

            }
            return;
        }
        
        // update our state
        this.myState = (FarkleState)info;
        updateDisplay();

        // check for a farkle in the current state
        boolean diceInPlay = false;
        for (Die d : myState.getDice()) {
            if (d.isInPlay()) {
                diceInPlay = true;
            }
        }
        if (diceInPlay && myState.hasFarkle()) {
            farkleImage1.setVisibility(View.VISIBLE);
            farkleImage2.setVisibility(View.VISIBLE);
            farkleImage1.invalidate();
            farkleImage2.invalidate();
            updateDisplay();
            this.lastFarkleId = myState.getCurrentPlayer();
            getTimer().setInterval(1500);
            getTimer().start();

//            if (myState.getCurrentPlayer() == playerNum) {
//                game.sendAction(new FarkleAction(this));
//            }
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
        
        // listeners
        for(ImageButton ib : diceButtons) { ib.setOnClickListener(this); }
        rollDiceButton.setOnClickListener(this);
        bankPointsButton.setOnClickListener(this);

        farkleImage1.setVisibility(View.INVISIBLE);
        farkleImage2.setVisibility(View.INVISIBLE);
        
    }
    
    /**
     * handle all click events -- actions made by player
     * @param button view that was clicked
     */
    public void onClick(View button) {
        
        // if we are not yet connected to a game, ignore
        if (game == null) return;
        int id = button.getId();
        
        // Construct the action and send it to the game
        GameAction action = null;
        
        if (id == R.id.bankPointsButton) {
            action = new BankPointsAction(this);
        }
        else if (id == R.id.rollDiceButton) {
            action = new RollAction(this);
        }
        // Checks for all image buttons
        else {
            for(int i=0; i<imageButtonId.length; i++) {
                if(id == imageButtonId[i]) {
                    action = new SelectDieAction(this, i);
                    break;
                }
            }
        }
        if (action != null) {
            game.sendAction(action); // send action to the game
        }
    }
    
    /**
     * display the die using the image id's
     */
    public void displayDie() {
        for(int i=0; i<6;i++) {
            Die curDie = myState.getDice()[i];
            if(!curDie.isInPlay()) {
                diceButtons[i].setVisibility(View.INVISIBLE);
            }
            else {
                diceButtons[i].setVisibility(View.VISIBLE);
                if(curDie.isSelected()) {
                    diceButtons[i].setImageResource(diceWhiteResID[curDie.getValue()-1]);
                }
                else {
                    if (myState.getCurrentPlayer() == playerNum) {
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
        if (myState.getCurrentPlayer() == 0) {//player 1's turn
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
        p0scoreText.setText(myState.getPlayerScores()[0]+"");
        p1scoreText.setText(myState.getPlayerScores()[1]+"");
        runningTotalText.setText(myState.getRunningTotal()+"");
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
         * Date: 	 12 April 2016
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
        if(diceStyle >=0 && diceStyle <6) {
            this.diceStyle = diceStyle;
            displayDie();
        }
    }
    
}
