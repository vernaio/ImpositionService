package de.perfectpattern.print.imposition.util;

import de.perfectpattern.print.imposition.model.type.Matrix;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.model.type.XYPair;
import de.perfectpattern.print.imposition.util.DimensionUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit test csae for DimensionUtil
 */
public class DimensionUtilTest {

    @Test
    public void mm2dtp_1() {

        // arrange

        // act
        final float result = DimensionUtil.mm2dtp(210f);

        // assert
        assertEquals("Millimeter value is wrong.", 595.2756f, result, 0.0001f);
    }

    @Test
    public void mm2dtp_2() {

        // arrange

        // act
        final float result = DimensionUtil.mm2dtp(297f);

        // assert
        assertEquals("Millimeter value is wrong.", 841.8898f, result, 0.0001f);
    }

    @Test
    public void micro2dtp_1() {

        // arrange

        // act
        final float result = DimensionUtil.micro2dtp(297000f);

        // assert
        assertEquals("Millimeter value is wrong.", 841.8898f, result, 0.0001f);
    }

    @Test
    public void dtp2mm() {

        // arrange

        // act
        final float result = DimensionUtil.dtp2mm(841.8898f);

        // assert
        assertEquals("Millimeter value is wrong.", 297.0000f, result, 0.0001f);
    }

    @Test
    public void dtp2cm() {

        // arrange

        // act
        final float result = DimensionUtil.dtp2cm(841.8898f);

        // assert
        assertEquals("Centimeter value is wrong.", 29.7f, result, 0.0001f);
    }

    @Test
    public void transform_point() {

        // arrange
        XYPair a = new XYPair(30, 100);
        Matrix trf = new Matrix(1, 0, 0, 1, 40, 60);

        // act
        XYPair b = DimensionUtil.transform(a, trf);

        // assert
        assertEquals("Transformation is wrong.", new XYPair(70, 160), b);
    }

    @Test
    public void transform_point_2() {

        // arrange
        XYPair a = new XYPair(11, 22);
        Matrix trf = new Matrix(33, 44, -55, -66, 77, 88);

        // act
        XYPair b = DimensionUtil.transform(a, trf);

        // assert
        assertEquals("Transformation is wrong.", new XYPair(-770, -880), b);
    }

    @Test
    public void transform_point_3() {

        // arrange
        XYPair a = new XYPair(30, 100);
        Matrix trf = new Matrix(1, 0, 0, -1, 0, 0);

        // act
        XYPair b = DimensionUtil.transform(a, trf);

        // assert
        System.out.println(String.format("Transformation: %s", b.toString()));

    }

    @Test
    public void transform_rectangle_1() {

        // arrange
        Rectangle a = new Rectangle(0, 0,3,5);
        Matrix trf = new Matrix(0, 1, -1, 0, 0, 0);

        // act
        Rectangle rectangle = DimensionUtil.transform(a, trf);

        // assert
        System.out.println(rectangle.toString());

        assertEquals("Transformation is wrong.", new Rectangle(-5f, 0, 0, 3f), rectangle);
    }

    @Test
    public void transform_rectangle_2() {

        // arrange
        Rectangle a = new Rectangle(1, 1,3,2);
        Matrix trf = new Matrix(0, 1, -1, 0, a.getHeight(), 0);

        // act
        Rectangle rectangle = DimensionUtil.transform(a, trf);

        // assert
        System.out.println(rectangle.toString());

        assertEquals("Transformation is wrong.", new Rectangle(1f, 1f, 2f, 3f), rectangle);
    }

    @Test
    public void transform_rectangle_3() {

        // arrange
        Rectangle a = new Rectangle(1, 1,3,2);
        Matrix trf = new Matrix(0, 1, -1, 0, a.getHeight(), 0);

        // act
        Rectangle rectangle = DimensionUtil.transform(a, trf);

        // assert
        System.out.println(rectangle.toString());

        assertEquals("Transformation is wrong.", new Rectangle(1f, 1f, 2f, 3f), rectangle);
    }

    @Test
    public void transform_rectangle_4() {

        // arrange
        Rectangle a = new Rectangle(1, 1,3,2);
        Matrix trf = new Matrix(0, 1, -1, 0, a.getUry(), 0);
        XYPair anchor = new XYPair();

        // act
        Rectangle rectangle = DimensionUtil.transform(a, trf, anchor);

        // assert
        System.out.println(rectangle.toString());

        assertEquals("Transformation is wrong.", new Rectangle(0f, 1f, 1f, 3f), rectangle);
    }

    @Test
    public void bytes2readable_1() {

        // act
        String result = DimensionUtil.bytes2readable(46554575);

        // assert
        assertEquals("Result is wrong.", "44.4 MB", result);

    }

    @Test
    public void bytes2readable_2() {

        // act
        String result = DimensionUtil.bytes2readable(1024);

        // assert
        assertEquals("Result is wrong.", "1.0 KB", result);

    }

    @Test
    public void bytes2readable_3() {

        // act
        String result = DimensionUtil.bytes2readable(1023);

        // assert
        assertEquals("Result is wrong.", "1023 B", result);

    }
}