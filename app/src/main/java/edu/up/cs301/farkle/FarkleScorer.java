package edu.up.cs301.farkle;

/**
 * This contains the scoring key for the farkle game.
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public abstract class FarkleScorer {

    /* ---=== Static Variables ===--- */
    public static final int SINGLE_1 = 100;
    public static final int SINGLE_5 = 50;
    private static final int TRIPLE_MULT = 100; // only exception -- three ones is 300
    public static final int FOUR_OFAKIND = 1000;
    public static final int FIVE_OFAKIND = 2000;
    public static final int SIX_OFAKIND = 3000;
    public static final int STRAIGHT = 1500;
    public static final int THREE_PAIR = 1500;
    public static final int FULL_HOUSE = 1500; // four of a kind and a pair
    public static final int TWO_TRIPLES = 2500;

    /**
     * calculate the score of a given triple
     * @param value value of the die that is a member of the triplet
     * @return the corresponding score for the triple
     */
    public static int tripleScore(int value) {
        if (value == 1) {
            return 300;
        } else {
            return value * TRIPLE_MULT;
        }
    }
}
