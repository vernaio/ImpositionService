package de.perfectpattern.print.imposition.model.type;

import de.perfectpattern.print.imposition.model.type.Priority;
import org.junit.Test;

import static org.junit.Assert.*;

public class PriorityTest {

    @Test
    public void getValue() {

        assertEquals("Value ST is wrong.", 0, Priority.Standard.getValue());
        assertEquals("Value EX is wrong.", 2, Priority.Express.getValue());
        assertEquals("Value ON is wrong.", 4, Priority.Overnight.getValue());
    }

    @Test
    public void findByValue() {

        assertEquals("Result is wrong.", Priority.Standard, Priority.findByValue(0));
        assertEquals("Result is wrong.", Priority.Express, Priority.findByValue(2));
        assertEquals("Result is wrong.", Priority.Overnight, Priority.findByValue(4));
        assertNull("Result is wrong.", Priority.findByValue(1));
        assertNull("Result is wrong.", Priority.findByValue(3));
    }

    @Test
    public void prio2String() {
        assertEquals("Result is wrong.", "Standard", Priority.Standard.toString());
    }
}