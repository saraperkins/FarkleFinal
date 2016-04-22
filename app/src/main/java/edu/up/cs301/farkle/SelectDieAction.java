package edu.up.cs301.farkle;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * represents a game action, where the player can select a single die
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 22 March 2016
 */
public class SelectDieAction extends GameAction {
    /* ---=== Instance ===--- */
    private int idxOfDie;

    /**
     * constructor for GameAction: select/deselect a die
     *
     * @param player the player who created the action
     * @param initIdx index of the die to be selected/deselected
     */
    public SelectDieAction(GamePlayer player, int initIdx) {
        super(player);
        idxOfDie = initIdx;
    }

    /* ---=== Accessor ===--- */
    public int getIdxOfDie() {
        return idxOfDie;
    }
}
