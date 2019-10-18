package de.perfectpattern.print.imposition.model.type;

import java.util.Objects;

public class Rectangle {

    private final float llx;

    private final float lly;

    private final float urx;

    private final float ury;

    /**
     * Default constructor.
     */
    public Rectangle() {
        this.llx = 0;
        this.lly = 0;
        this.urx = 0;
        this.ury = 0;
    }

    /**
     * Custom constructor, accepting values for page coordinates.
     *
     * @param llx Lower-left x.
     * @param lly Lower-left y.
     * @param urx Upper-right x.
     * @param ury Upper-right y.
     */
    public Rectangle(float llx, float lly, float urx, float ury) {
        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;
    }

    /**
     * Custom Constructor. Creates a new Rectangle instance by a XJDF String expression.
     *
     * @param expression Rectangle as String expression.
     */
    public Rectangle(String expression) {

        // split string
        String[] s = expression.split(" ");

        // extract values
        this.llx = Float.valueOf(s[0]);
        this.lly = Float.valueOf(s[1]);
        this.urx = Float.valueOf(s[2]);
        this.ury = Float.valueOf(s[3]);
    }

    public float getLlx() {
        return llx;
    }

    public float getLly() {
        return lly;
    }

    public float getUrx() {
        return urx;
    }

    public float getUry() {
        return ury;
    }

    public float getWidth() {
        return urx - llx;
    }

    public float getHeight() {
        return ury - lly;
    }

    public XYPair getSize() { return new XYPair(getWidth(), getHeight());}

    @Override
    public String toString() {
        return llx + " " + lly + " " + urx + " " + ury;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return Float.compare(rectangle.llx, llx) == 0 &&
                Float.compare(rectangle.lly, lly) == 0 &&
                Float.compare(rectangle.urx, urx) == 0 &&
                Float.compare(rectangle.ury, ury) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(llx, lly, urx, ury);
    }
}
