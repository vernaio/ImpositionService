package de.perfectpattern.print.imposition.model.type;

import java.util.Objects;

/**
 * XJDF XYPair data type.
 */
public class XYPair {

    private final float x;
    private final float y;

    /**
     * Default constructor.
     */
    public XYPair() {
        this(0,0);
    }

    /**
     * Custom constructor.
     * @param x The X value.
     * @param y The y value.
     */
    public XYPair(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    /**
     * Adds another XYPair the the current one.
     * @param other The other one.
     * @return The sum of both XYPairs.
     */
    public XYPair add(XYPair other) {
        return new XYPair(
                this.getX() + other.getX(),
                this.getY() + other.getY()
        );
    }

    /**
     * Subtracts another XYPair from the current one.
     * @param other The other one.
     * @return The difference of both XYPairs.
     */
    public XYPair subtract(XYPair other) {
        return new XYPair(
                this.getX() - other.getX(),
                this.getY() - other.getY()
        );
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XYPair xyPair = (XYPair) o;
        return Float.compare(xyPair.x, x) == 0 &&
                Float.compare(xyPair.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
