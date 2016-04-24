package edu.up.cs301.farkle;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * represents an action in the game, where the player banks the points in the running
 * total to his/her permanent score
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 8 April 2016
 */
public class FarkleAction extends GameAction {
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public FarkleAction(GamePlayer player) {
        super(player);
    }
}
