package edu.up.cs301.farkle;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * tests the Die class
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 15 March 2016
 */
public class DieTest {

    @Test
    public void testRoll() throws Exception {
        Die die = new Die();
        for (int i = 0; i < 500; i++) {
            die.roll();
            assertTrue(die.getValue() < 7 && die.getValue() > 0);
        }
    }

    @Test
    public void testDie() throws Exception {
        Die die = new Die();
        assertTrue(die.getValue() < 7 && die.getValue() > 0);
        assertTrue(!die.isInPlay());
        assertTrue(!die.isSelected());

    }

    @Test
    public void testDieCpy() throws Exception {
        Die die1 = new Die();
        int die1val = die1.getValue();
        Die die2 = new Die(die1);

        // unchanged
        assertTrue(die1val == die2.getValue());
        assertTrue(!die2.isSelected());
        assertTrue(!die2.isInPlay());

        // change die 1
        die1.setIsSelected(true);
        assertTrue(die1.getValue() == die1val);
        assertTrue(!die1.isInPlay());
        assertTrue(die1.isSelected());
        assertTrue(!die2.isSelected());

        // change die 2
        die2 = new Die(die1);
        assertTrue(die1.getValue() == die2.getValue());
        assertTrue(die2.isSelected());
        assertTrue(die1.isSelected());
        assertTrue(!die2.isInPlay());
    }

    @Test
    public void testReset() {
        Die die = new Die();
        die.setIsSelected(true);
        die.setIsInPlay(false);
        die.reset();
        assertFalse(die.isSelected());
        assertTrue(die.isInPlay());
    }
}