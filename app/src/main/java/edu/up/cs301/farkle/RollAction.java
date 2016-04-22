package edu.up.cs301.farkle;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * represents a roll action in the game, where the player can roll all dice currently in play
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 22 March 2016
 */
public class RollAction extends GameAction {
    /**
     * constructor for GameAction: roll dice
     *
     * @param player the player who created the action
     */
    public RollAction(GamePlayer player) {
        super(player);
    }
}
