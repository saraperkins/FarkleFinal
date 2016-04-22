package edu.up.cs301.farkle;

import java.io.Serializable;

/**
 * represents a single die object in the farkle game
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public class Die implements Serializable {

    private static final long SerialVersionUID = 7835399752863891224L;
    private boolean isInPlay, isSelected;
    private int value;

    /**
     * constructor for a single Die object
     */
    public Die () {
        isInPlay = false;
        isSelected = false;
        roll(); // initialize the value of the die
    }

    /**
     * deep copy constructor for a single die object
     * @param cpy die to make the copy of
     */
    public Die (Die cpy) {
        isInPlay = cpy.isInPlay();
        isSelected = cpy.isSelected();
        value = cpy.getValue();
    }

    /**
     * generate a random die value
     * @return int between 1-6
     */
    public void roll() {
        value = (int) (Math.random() * 6) + 1;
    }

    /**
     * reset the die object to original state
     */
    public void reset() {
        isInPlay = true;
        isSelected = false;
        roll();
    }


    /* ---=== Accessors ===--- */
    public boolean isSelected() {
        return isSelected;
    }

    public int getValue() {
        return value;
    }

    /* ---=== Setters ===--- */
    public boolean isInPlay() {
        return isInPlay;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setIsInPlay(boolean isInPlay) {
        this.isInPlay = isInPlay;
    }

    public void setValue(int val) {
        value = val;
    }


}
