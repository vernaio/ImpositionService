package de.perfectpattern.print.imposition.model.type;

import de.perfectpattern.print.imposition.model.type.XYPair;
import org.junit.Test;

import static org.junit.Assert.*;

public class XYPairTest {

    @Test
    public void add() {

        // arrange
        XYPair a = new XYPair(1,2);
        XYPair b = new XYPair(3,4);

        // act
        XYPair c = a.add(b);

        // assert
        assertEquals("Result is wrong.", new XYPair(4,6), c);
    }

    @Test
    public void subtract() {

        // arrange
        XYPair a = new XYPair(1,2);
        XYPair b = new XYPair(3,5);

        // act
        XYPair c = a.subtract(b);

        // assert
        assertEquals("Result is wrong.", new XYPair(-2,-3), c);
    }
}