package de.perfectpattern.print.imposition.model.type;

import de.perfectpattern.print.imposition.model.type.Orientation;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrientationTest {

    @Test
    public void findByDegree() {
        assertEquals("Orientation is wrong.", Orientation.Rotate0, Orientation.findByDegree(0));
        assertEquals("Orientation is wrong.", Orientation.Rotate90, Orientation.findByDegree(90));
        assertEquals("Orientation is wrong.", Orientation.Rotate180, Orientation.findByDegree(180));
        assertEquals("Orientation is wrong.", Orientation.Rotate270, Orientation.findByDegree(270));
        assertEquals("Orientation is wrong.", Orientation.Rotate0, Orientation.findByDegree(360));
        assertEquals("Orientation is wrong.", Orientation.Rotate90, Orientation.findByDegree(450));
        assertEquals("Orientation is wrong.", Orientation.Rotate0, Orientation.findByDegree(720));
        assertEquals("Orientation is wrong.", Orientation.Rotate270, Orientation.findByDegree(-90));
        assertEquals("Orientation is wrong.", Orientation.Rotate180, Orientation.findByDegree(-180));
        assertEquals("Orientation is wrong.", Orientation.Rotate90, Orientation.findByDegree(-270));
        assertEquals("Orientation is wrong.", Orientation.Rotate270, Orientation.findByDegree(-450));

        assertNull("Orientation is wrong.", Orientation.findByDegree(22));
    }
}