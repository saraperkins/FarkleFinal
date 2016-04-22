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
 * @version 22 March 2016
 */
public class BankPointsAction extends GameAction{
    /**
     * constructor for GameAction: bank points
     *
     * @param player the player who created the action
     */
    public BankPointsAction(GamePlayer player) {
        super(player);
    }
}
