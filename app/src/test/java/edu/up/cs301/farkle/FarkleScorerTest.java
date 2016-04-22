package edu.up.cs301.farkle;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * tests the scoring key for farkle
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public class FarkleScorerTest {

    @Test
    public void testTripleScore() throws Exception {
        for (int i = 2; i < 7; i++) {
            assertEquals(i*100, FarkleScorer.tripleScore(i));
        }

        assertEquals(300, FarkleScorer.tripleScore(1));
    }
}