package de.perfectpattern.print.imposition.model.type;

public class Matrix {

    private final float a;
    private final float b;
    private final float c;
    private final float d;
    private final float e;
    private final float f;

    /**
     * Default constructor.
     */
    public Matrix() {
        this(1,0,0,1,0,0);
    }

    /**
     * Custom constructor.,
     * @param matrix The other matrix.
     */
    public Matrix(Matrix matrix){
        this.a = matrix.getA();
        this.b = matrix.getB();
        this.c = matrix.getC();
        this.d = matrix.getD();
        this.e = matrix.getE();
        this.f = matrix.getF();
    }

    /**
     * Custom constructor. Accepting all parameters of the matrix.
     */
    public Matrix(float a, float b, float c, float d, float e, float f) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    /**
     * Custom constructor. Accepting rectangle and orientation details for initializing.
     * @param orientation The orientation of the CTM.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public Matrix(Orientation orientation, float width, float height) {

        if (Orientation.Rotate90.equals(orientation)) {
            this.a = 0;
            this.b = 1;
            this.c = -1;
            this.d = 0;
            this.e = height;
            this.f = 0;

        } else if (Orientation.Rotate180.equals(orientation)) {
            this.a = -1;
            this.b = 0;
            this.c = 0;
            this.d = -1;
            this.e = width;
            this.f = height;

        } else if (Orientation.Rotate270.equals(orientation)) {
            this.a = 0;
            this.b = -1;
            this.c = 1;
            this.d = 0;
            this.e = 0;
            this.f = width;

        } else {
            this.a = 1;
            this.b = 0;
            this.c = 0;
            this.d = 1;
            this.e = 0;
            this.f = 0;

        }

    }


    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getC() {
        return c;
    }

    public float getD() {
        return d;
    }

    public float getE() {
        return e;
    }

    public float getF() {
        return f;
    }
}
