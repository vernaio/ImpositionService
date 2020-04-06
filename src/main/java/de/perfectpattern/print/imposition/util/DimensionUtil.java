package de.perfectpattern.print.imposition.util;

import de.perfectpattern.print.imposition.model.type.Matrix;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.model.type.XYPair;

public class DimensionUtil {

    /**
     * Converts a millimeter value in dtp points.
     *
     * @param millimeter The millimeter value.
     * @return The value in dtp points.
     */
    public static float mm2dtp(float millimeter) {
        return millimeter * 72 / 25.4f;
    }

    /**
     * Converts a micrometer value in dtp points.
     *
     * @param micrometer The micrometer value.
     * @return The value in dtp points.
     */
    public static float micro2dtp(float micrometer) {
        return mm2dtp(micrometer / 1000);
    }

    /**
     * Converts a dtp points value in millimeters
     *
     * @param dtp The dtp points value.
     * @return The appropriate value in millimeters.
     */
    public static float dtp2mm(float dtp) {
        return dtp * 25.4f / 72f;
    }

    /**
     * Converts a dtp points value in centimeters
     *
     * @param dtp The dtp points value.
     * @return The appropriate value in centimeters.
     */
    public static float dtp2cm(float dtp) {
        return dtp2mm(dtp) / 10;
    }

    /**
     * Transform a two dimensional point by a given matrix.
     *
     * @param point The point to be transferred.
     * @param ctm Specifies the transformation matrix of the origin of Content as specified by origin (0,0)
     * @return The transformed point.
     */
    public static XYPair transform(XYPair point, Matrix ctm) {
        return transform(point, ctm, new XYPair(0,0));
    }

    /**
     * Transform a two dimensional point by a given matrix.
     *
     * @param point The point to be transferred.
     * @param ctm Specifies the transformation matrix of the origin of Content as specified by anchor
     * @param anchor Specifies the origin (0,0) of the coordinate system in the unrotated Content.
     * @return The transformed point.
     */
    public static XYPair transform(XYPair point, Matrix ctm, XYPair anchor) {
        float x = point.subtract(anchor).getX();
        float y = point.subtract(anchor).getY();

        float a = ctm.getA();
        float b = ctm.getB();
        float c = ctm.getC();
        float d = ctm.getD();
        float e = ctm.getE();
        float f = ctm.getF();

        return new XYPair(
                x * a + y * c + 1 * e,
                x * b + y * d + 1 * f
        ).add(anchor);
    }

    /**
     * Transform a rectangle by a given matrix.
     *
     * @param rectangle The rectangle to be transferred.
     * @param ctm       The given transformation matrix.
     * @return The transformed rectangle.
     */
    public static Rectangle transform(Rectangle rectangle, Matrix ctm) {
        return transform(rectangle, ctm, null);
    }

    /**
     * Transform a rectangle by a given matrix.
     *
     * @param rectangle The rectangle to be transferred.
     * @param ctm       The given transformation matrix.
     * @param anchor    The anchor point of the rotation.
     * @return The transformed rectangle.
     */
    public static Rectangle transform(Rectangle rectangle, Matrix ctm, XYPair anchor) {
        XYPair ll = new XYPair(rectangle.getLlx(), rectangle.getLly());
        XYPair ur = new XYPair(rectangle.getUrx(), rectangle.getUry());
        XYPair ac = anchor == null ? ll : anchor;

        XYPair r1 = transform(ur, ctm, ac);
        XYPair r2 = transform(ll, ctm, ac);


        float llx = r1.getX() < r2.getX() ? r1.getX() : r2.getX();
        float urx = r1.getX() >= r2.getX() ? r1.getX() : r2.getX();

        float lly = r1.getY() < r2.getY() ? r1.getY() : r2.getY();
        float ury = r1.getY() >= r2.getY() ? r1.getY() : r2.getY();


        return new Rectangle(llx, lly, urx, ury);
    }

    /**
     * Convert bytes in a human readable form.
     * @param bytes The number of bytes.
     * @return The bytes in a human readable form.
     */
    public static String bytes2readable(long bytes) {
        String result;

        if (bytes < 1024) {
            result = bytes + " B";
        } else {
            int exp = (int) (Math.log(bytes) / Math.log(1024));

            result = String.format(
                    "%.1f %sB",
                    bytes / Math.pow(1024, exp),
                    ("KMGTPE").charAt(exp-1)
            );
        }

        return result;
    }
}
