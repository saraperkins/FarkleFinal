package edu.up.cs301.farkle;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * tests the FarkleGameState class
 *
 * @author Alexa Baldwin
 * @author Levi Banks
 * @author Sara Perkins
 * @author Briahna Santillana
 * @version 21 April 2016
 */
public class FarkleStateTest extends TestCase {
    @Test
    public void testFarkleState() throws Exception {
        FarkleState farkleState = new FarkleState();
        assertEquals(0, farkleState.getPlayerScores()[0]);
        assertEquals(0, farkleState.getPlayerScores()[1]);
        assertEquals(0, farkleState.getRunningTotal());
        assertEquals(0, farkleState.getCurrentPlayer());
        for(Die die : farkleState.getDice()) {
            assertTrue(!die.isInPlay());
        }
    }

    @Test
    public void testFarkleStateCpy() throws Exception {
        FarkleState farkleState1 = new FarkleState();
        FarkleState farkleState2 = new FarkleState(farkleState1);

        //initial
        assertEquals(farkleState1.getPlayerScores()[0], farkleState2.getPlayerScores()[0]);
        assertEquals(farkleState1.getPlayerScores()[1], farkleState2.getPlayerScores()[1]);
        assertEquals(farkleState1.getRunningTotal(), farkleState2.getRunningTotal());
        assertEquals(farkleState1.getCurrentPlayer(), farkleState2.getCurrentPlayer());

        //change P0score
        farkleState1.setPlayerScores(0, 500);//setP0score(500);
        assertEquals(500, farkleState1.getPlayerScores()[0]);
        assertEquals(0, farkleState2.getPlayerScores()[0]);

        farkleState2 = new FarkleState(farkleState1);
        assertEquals(500, farkleState2.getPlayerScores()[0]);

        //change P1score
        farkleState1.setPlayerScores(1, 500);//setP1score(500);
        assertEquals(500, farkleState1.getPlayerScores()[1]);
        assertEquals(0, farkleState2.getPlayerScores()[1]);

        farkleState2 = new FarkleState(farkleState1);
        assertEquals(500, farkleState2.getPlayerScores()[1]);

        //change runningtotal
        farkleState1.setRunningTotal(500);
        assertEquals(500, farkleState1.getRunningTotal());
        assertEquals(0, farkleState2.getRunningTotal());

        farkleState2 = new FarkleState(farkleState1);
        assertEquals(500, farkleState2.getRunningTotal());

        //change currentplayer
        farkleState1.setCurrentPlayer(1);
        assertEquals(1, farkleState1.getCurrentPlayer());
        assertEquals(0, farkleState2.getCurrentPlayer());

        farkleState2 = new FarkleState(farkleState1);
        assertEquals(1, farkleState2.getCurrentPlayer());

        //change isselected
        Die[] dice = farkleState1.getDice();
        Die[] dice2 = farkleState2.getDice();
        boolean isSelected = dice[4].isSelected();
        dice[4].setIsSelected(!dice[4].isSelected());
        assertTrue(dice[4].isSelected() == !isSelected);
        assertTrue(dice2[4].isSelected() == isSelected);

        farkleState2 = new FarkleState(farkleState1);
        dice2 = farkleState2.getDice();
        assertTrue(dice2[4].isSelected() == !isSelected);
    }

    @Test
    public void testNoDieSelectedInCurrentTurn() throws Exception {
        FarkleState farkleState = new FarkleState();
        for (Die d: farkleState.getDice()) {
            d.setIsInPlay(true);
        }
        assertTrue(farkleState.noDieSelectedInCurrentTurn());
        farkleState.getDice()[2].setIsSelected(true);
        assertFalse(farkleState.noDieSelectedInCurrentTurn());
    }

    @Test
    public void testRollDice() throws Exception {
        FarkleState farkleState = new FarkleState();
        int dieVal = 1;
        farkleState.getDice()[0].setValue(dieVal);
        for (Die d: farkleState.getDice()) {
            d.setIsInPlay(true);
        }

        // single selected die test
        farkleState.selectDie(0);
        farkleState.rollDice();
        assertEquals(dieVal, farkleState.getDice()[0].getValue());
        assertFalse(farkleState.getDice()[0].isInPlay());

        // set all the rest of the die to a value of one and select
        for (int i = 0; i < 6; i++) {
            if (farkleState.getDice()[i].isInPlay()) {
                farkleState.getDice()[i].setValue(dieVal);
                farkleState.getDice()[i].setIsSelected(true);
            }
        }

        farkleState.setRunningTotal(3000);

        farkleState.rollDice();

        // check that the dice can be rolled if all are selected and valid
        for (Die die: farkleState.getDice()) {
            assertTrue(die.isInPlay());
        }
    }

    @Test
    public void testCpy6intarray() throws Exception {
        FarkleState farkleState = new FarkleState();
        int[] arrayCpy = {0,0,0,0,0,0};
        int[] arrayDest = {1, 2, 3, 4, 5, 6};

        farkleState.cpy6intarray(arrayCpy, arrayDest);
        for (int i = 0; i < 6; i++) {
            assertEquals(arrayCpy[i], arrayDest[i]);
        }
        arrayDest[1] = 8;
        assertTrue(arrayDest[1] != arrayCpy[1]);

    }

    @Test
    public void testSelectDie() throws Exception {
        // set up the state
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(3);
        for (Die d: farkleState.getDice()) {
            d.setIsInPlay(true);
        }

        // select invalid die
        farkleState.selectDie(3);
        assertTrue(farkleState.getDice()[3].isSelected());
        assertEquals(0, farkleState.getRunningTotal());
        // deselect last die
        farkleState.selectDie(3);
        assertFalse(farkleState.getDice()[3].isSelected());
        assertEquals(0, farkleState.getRunningTotal());

        // select the triple
        farkleState.selectDie(0);
        farkleState.selectDie(1);
        farkleState.selectDie(2);

        assertEquals(200, farkleState.getRunningTotal());

        // extend selection to combo
        farkleState.selectDie(4);
        assertEquals(250, farkleState.getRunningTotal());

        // create and select a double triple
        farkleState.getDice()[3].setValue(3);
        farkleState.getDice()[4].setValue(3);
        farkleState.getDice()[5].setValue(3);
        farkleState.selectDie(3);
        farkleState.selectDie(5);

        assertEquals(2500, farkleState.getRunningTotal());
    }

    @Test
    public void testBankPoints() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(3);

        for (Die d: farkleState.getDice()) {
            d.setIsInPlay(true);
        }

        farkleState.setCurrentPlayer(0);
        farkleState.setRunningTotal(500);

        //makes sure it returns false if nothing is selected
        assertFalse(farkleState.bankPoints());

        //makes sure it returns false with an invalid dice selection
        farkleState.setRunningTotal(0);
        farkleState.selectDie(0);
        assertFalse(farkleState.bankPoints());

        farkleState.setRunningTotal(1);
        farkleState.setRunningTotal(2);
        farkleState.selectDie(1);
        farkleState.selectDie(2); //now we have valid selection
        assertTrue(farkleState.bankPoints());
        assertTrue(farkleState.getRunningTotal() == 0);
        assertEquals(farkleState.getPreselectRunningTotal(), 0);
        assertTrue(farkleState.getCurrentPlayer() == 1);

    }

    @Test
    public void testFarkle() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.setCurrentPlayer(1);
        farkleState.farkle();
        assertTrue(farkleState.getCurrentPlayer() == 0);
        farkleState.farkle();
        assertTrue(farkleState.getCurrentPlayer() == 1);
        assertTrue(farkleState.getRunningTotal() == 0);
        assertTrue(farkleState.getPreselectRunningTotal()==0);
    }

    @Test
    public void testHasFarkle() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(6);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(6);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.hasFarkle());
        farkleState.getDice()[1].setValue(1);
        assertFalse(farkleState.hasFarkle());
    }

    @Test
    public void testUpdateCount() throws Exception {
        FarkleState farkleState = new FarkleState();
        int i = 0;
        for(Die die: farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setValue(i + 1);
            i++;
        }

        farkleState.updateCount(true);
        int[] count = farkleState.getCount();
        for (i = 0; i < count.length; i++) {
            assertEquals(1, count[i]);
        }


    }

    @Test
    public void testThreeOfAKind() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }
        assertTrue(farkleState.threeOfAKind(true) == 2);
        farkleState.getDice()[1].setValue(1);
        assertTrue(farkleState.threeOfAKind(true) == -1);
    }
    @Test
    public void testFourOfAKind() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(2);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.fourOfAKind(true));
        farkleState.getDice()[1].setValue(5);
        assertFalse(farkleState.fourOfAKind(true));
    }

    @Test
    public void testFiveOfAKind() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(4);
        farkleState.getDice()[1].setValue(4);
        farkleState.getDice()[2].setValue(4);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(4);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.fiveOfAKind(true));
        farkleState.getDice()[1].setValue(2);
        assertFalse(farkleState.fiveOfAKind(true));
    }

    @Test
    public void testSixOfAKind() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(3);
        farkleState.getDice()[1].setValue(3);
        farkleState.getDice()[2].setValue(3);
        farkleState.getDice()[3].setValue(3);
        farkleState.getDice()[4].setValue(3);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.sixOfAKind(true));
        farkleState.getDice()[1].setValue(2);
        assertFalse(farkleState.sixOfAKind(true));
    }

    @Test
    public void testStraight() throws Exception {
        FarkleState farkleState = new FarkleState();
        for(int i=0;i<6;i++) {
            farkleState.getDice()[i].setValue(i+1);
        }
        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.straight(true));
        farkleState.getDice()[1].setValue(6);
        assertFalse(farkleState.straight(true));
    }

    @Test
    public void testThreePairs() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(4);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(5);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.threePairs(true));
        farkleState.getDice()[1].setValue(1);
        assertFalse(farkleState.threePairs(true));
    }

    @Test
    public void testTwoTriplets() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(2);
        farkleState.getDice()[1].setValue(2);
        farkleState.getDice()[2].setValue(2);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(4);
        farkleState.getDice()[5].setValue(4);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.twoTriplets(true));
        farkleState.getDice()[1].setValue(1);
        assertFalse(farkleState.twoTriplets(true));
    }

    @Test
    public void testFourAndPair() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(1);
        farkleState.getDice()[1].setValue(1);
        farkleState.getDice()[2].setValue(1);
        farkleState.getDice()[3].setValue(1);
        farkleState.getDice()[4].setValue(2);
        farkleState.getDice()[5].setValue(2);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.fourAndPair(true));
        farkleState.getDice()[2].setValue(6);
        assertFalse(farkleState.fourAndPair(true));
    }

    @Test
    public void testNumSingles() throws Exception {
        FarkleState farkleState = new FarkleState();
        farkleState.getDice()[0].setValue(1);
        farkleState.getDice()[1].setValue(1);
        farkleState.getDice()[2].setValue(5);
        farkleState.getDice()[3].setValue(4);
        farkleState.getDice()[4].setValue(5);
        farkleState.getDice()[5].setValue(3);

        for(Die die : farkleState.getDice()) {
            die.setIsInPlay(true);
            die.setIsSelected(true);
        }

        assertTrue(farkleState.numSingles(5, true) == 2);
        farkleState.getDice()[2].setValue(6);
        assertTrue(farkleState.numSingles(5, true) == 1);
        assertTrue(farkleState.numSingles(1, true) == 2);
        farkleState.getDice()[5].setValue(1);
        assertTrue(farkleState.numSingles(1, true) == 3);

    }

    @Test
    public void testMichaelTest() throws Exception {
        FarkleState fs = new FarkleState();
        Die[] dice = fs.getDice();
        for (int i = 0; i < 3; i++) {
            dice[i].setIsInPlay(true);
        }

        dice[0].setValue(1);
        dice[1].setValue(1);
        dice[2].setValue(6);

        for (int i = 0; i < 3; i++) {
            fs.selectDie(i);
            if (i < 2) {
                assertEquals(100 * (i+1), fs.getRunningTotal());
            } else {
                assertEquals(0, fs.getRunningTotal());
            }
        }

        for (int i = 0; i < 3; i++) {
            assertTrue(dice[i].isSelected());
        }
        assertEquals(0, fs.getCurrentPlayer());
        fs.bankPoints();
        assertEquals(0, fs.getPlayerScores()[0]);

        assertEquals(0, fs.getCurrentPlayer());

    }
}

