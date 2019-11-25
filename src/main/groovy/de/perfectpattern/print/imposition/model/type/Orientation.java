package de.perfectpattern.print.imposition.model.type;

/**
 * Enum for orientation data type.
 */
public enum Orientation {
    Rotate0(0),
    Rotate90(90),
    Rotate180(180),
    Rotate270(270);

    private final int degree;

    Orientation(int degree) {
        this.degree = degree;
    }

    public int getDegree() {
        return degree;
    }

    /**
     * Returns the rotation by a degree value..
     * @param degree The degree value.
     * @return The appropriate Orientation as enum.
     */
    public static Orientation findByDegree(int degree){
        int d = degree % 360;

        if(d == 0) {
            return Rotate0;
        } else if (d == 90 || d == -270) {
            return Rotate90;
        } else if (d == 180 || d == -180) {
            return Rotate180;
        } else if (d == 270 || d == -90) {
            return Rotate270;
        }

        return null;
    }

}
