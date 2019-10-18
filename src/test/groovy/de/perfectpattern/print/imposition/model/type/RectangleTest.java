package de.perfectpattern.print.imposition.model.type;

import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.model.type.XYPair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * JUnit test case for Rectangle
 */
public class RectangleTest {


    @Test
    public void testNewInstance() {

        // arrange

        // act
        Rectangle r = new Rectangle();

        // assert
        assertEquals("Llx is wrong.", 0f, r.getLlx(), 0.01);
        assertEquals("Lly is wrong.", 0f, r.getLly(), 0.01);
        assertEquals("Urx is wrong.", 0f, r.getUrx(), 0.01);
        assertEquals("Ury is wrong.",0f, r.getUry(), 0.01);
    }

    @Test
    public void testNewInstanceString() {

        // arrange
        final String value = "1 0 3.14 21631.3";

        // act
        Rectangle r = new Rectangle(value);

        // assert
        assertEquals("Llx is wrong.", 1f, r.getLlx(), 0.01);
        assertEquals("Lly is wrong.", 0f, r.getLly(), 0.01);
        assertEquals("Urx is wrong.", 3.14f, r.getUrx(), 0.01);
        assertEquals("Ury is wrong.", 21631.3f, r.getUry(), 0.01);
    }

    @Test
    public void testNewInstanceFloat() {

        // arrange

        // act
        Rectangle r = new Rectangle(1f, 0f, 3.14f, 21631.3f);

        // assert
        assertEquals("Llx is wrong.", 1f, r.getLlx(), 0.01);
        assertEquals("Lly is wrong.", 0f, r.getLly(), 0.01);
        assertEquals("Urx is wrong.", 3.14f, r.getUrx(), 0.01);
        assertEquals("Ury is wrong.", 21631.3f, r.getUry(), 0.01);
    }

    @Test
    public void testHeight() {

        // arrange
        final String value = "1 2 4 8";

        // act
        float height = new Rectangle(value).getHeight();

        // assert
        assertEquals("Height is wrong.", 6, height, 0.01f);
    }

    @Test
    public void testWidth() {

        // arrange
        final String value = "1 2 4 8";

        // act
        float width = new Rectangle(value).getWidth();

        // assert
        assertEquals("Width is wrong.", 3, width, 0.01f);
    }

    @Test
    public void testSize() {

        // arrange
        final String value = "1 2 4 8";

        // act
        XYPair size = new Rectangle(value).getSize();

        // assert
        assertEquals("Width is wrong.", 3, size.getX(), 0.01f);
        assertEquals("Height is wrong.", 6, size.getY(), 0.01f);
    }
}