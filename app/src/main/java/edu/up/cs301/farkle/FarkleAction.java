package edu.up.cs301.farkle;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by levibanks on 4/8/16.
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
