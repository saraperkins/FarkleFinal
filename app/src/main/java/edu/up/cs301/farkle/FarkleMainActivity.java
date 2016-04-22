package edu.up.cs301.farkle;

import android.graphics.Color;
import android.media.Image;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.R;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * This contains the activity for the Farkle game.
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public class FarkleMainActivity extends GameMainActivity {
    // for networked play
    private static final int PORT_NUMBER = 2234;
    protected static ImageView playerOneImage, playerTwoImage;
    private FarkleHumanPlayer guiPlayer;

    public void setGuiPlayer(FarkleHumanPlayer p) {
        guiPlayer = p;
    }

    //or I can do references of some kind in GameState
    
    /**
     * Create the default configuration for this game:
     * - one human player vs. one computer player
     * - minimum of 1 player, maximum of 2
     * - one kind of computer player and one kind of human player available
     *
     * @return the new configuration object, representing the default configuration
     */
    @Override
    public GameConfig createDefaultConfig() {
        
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
        
        // Farkle has two player types:  human and computer
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new FarkleHumanPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new FarkleDumbComputerPlayer(name);
            }});
        playerTypes.add(new GamePlayerType("Hard Computer Player") {
            public GamePlayer createPlayer(String name) {
                return new FarkleSmartComputerPlayer(name);
            }});
        
        
        // Create a game configuration class for Farkle:
        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 2, "Farkle", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer Smart", 2); // player 2: a computer player
        defaultConfig.setRemoteData("Remote Human Player", "", 0);
        
        
        return defaultConfig;
    }
    
    /**
     * create a local game
     *
     * @return the local farkle game
     */
    @Override
    public LocalGame createLocalGame() {
        return new FarkleLocalGame();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        //image views
        playerOneImage = FarkleHumanPlayer.playerOneImage;
        playerTwoImage = FarkleHumanPlayer.playerTwoImage;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        if (id == R.id.score_guide) {
            Toast score_guide = Toast.makeText(getApplicationContext(), "5’s = 50 points\n" +
                                               "1’s = 100 points\n" +
                                               "1,1,1 = 300 points\n" +
                                               "2,2,2 = 200 points\n" +
                                               "3,3,3 = 300 points\n" +
                                               "4,4,4 = 400 points\n" +
                                               "5,5,5 = 500 points\n" +
                                               "6,6,6 = 600 points\n" +
                                               "Four of a Kind = 1,000 points\n" +
                                               "Five of a Kind = 2,000 points\n" +
                                               "Six of a Kind = 3,000 points\n" +
                                               "A Straight of 1-6 = 1,500 points\n" +
                                               "Three Pairs = 1,500 points\n" +
                                               "Four of a Kind + a Pair = 1,500\n" +
                                               "Two sets of Three of a Kind = 2,500", Toast.LENGTH_LONG);
            score_guide.setGravity(Gravity.TOP|Gravity.LEFT, 800, 400);
            
            score_guide.show();
        }
        
        //noinspection SimplifiableIfStatement
        else if (id == R.id.girl) {
            playerOneImage.setImageResource(R.drawable.avatar_girl);
        }
        else if (id == R.id.boyBlackHair)
        {
            playerOneImage.setImageResource(R.drawable.avatar_boy1);
        }
        else if (id == R.id.boyRedHair)
        {
            playerOneImage.setImageResource(R.drawable.avatar_boy2);
        }
        else if (id == R.id.dog)
        {
            playerOneImage.setImageResource(R.drawable.avatar_puppy);
        }
        else if (id == R.id.girl1) {
            playerTwoImage.setImageResource(R.drawable.avatar_girl);
        }
        else if (id == R.id.boyBlackHair1)
        {
            playerTwoImage.setImageResource(R.drawable.avatar_boy1);
        }
        else if (id == R.id.boyRedHair1)
        {
            playerTwoImage.setImageResource(R.drawable.avatar_boy2);
        }
        else if (id == R.id.dog1)
        {
            playerTwoImage.setImageResource(R.drawable.avatar_puppy);
        }
        else if (id == R.id.cat) {
            playerTwoImage.setImageResource(R.drawable.avatar_cat);
        }
        else if (id == R.id.cat2) {
            playerOneImage.setImageResource(R.drawable.avatar_cat);
        }
        else if (id == R.id.girl2) {
            playerTwoImage.setImageResource(R.drawable.avatar_girl2);
        }
        else if (id == R.id.girl3) {
            playerOneImage.setImageResource(R.drawable.avatar_girl2);
        }
        else if (id == R.id.pinkDie)
        {
            if(guiPlayer!=null) {
                guiPlayer.setDiceStyle(1);
            }
        }
        else if (id == R.id.sunsetDie)
        {
            if(guiPlayer!=null) {
                guiPlayer.setDiceStyle(2);
            }
        }
        else if (id == R.id.purpleDie)
        {
            if(guiPlayer!=null) {
                guiPlayer.setDiceStyle(3);
            }
        }
        else if (id == R.id.redDie)
        {
            if(guiPlayer!=null) {
                guiPlayer.setDiceStyle(0);
            }
        }
        else if (id == R.id.nuxDie) {
            double x = Math.random();
            if(guiPlayer != null) {
                if(x>0.5)
                    guiPlayer.setDiceStyle(4);
                else
                    guiPlayer.setDiceStyle(5);
            }
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
