package de.perfectpattern.print.imposition.model.type;

import com.lowagie.text.pdf.CMYKColor;

public enum Color {
    CYAN(1,0,0,0),
    MAGENTA(0,1,0,0),
    YELLOW(0,0,1,0),
    BLACK(0,0,0,1),
    RED(0, 1, 1, 0),
    GREEN(1, 0, 1, 0),
    BLUE(1, 1, 0, 0),
    WHITE(0,0,0,0),
    GRAY(0,0,0,0.6f),

    STANDARD(.5f,0f,.5f,.1f),
    EXPRESS(0f,0f,0.75f,.02f),
    OVERNIGHT(0f,0.5f,0.5f,0.1f);

    private final float c;
    private final float m;
    private final float y;
    private final float k;

    Color(float c, float m, float y, float k) {
        this.c = c;
        this.m = m;
        this.y = y;
        this.k = k;
    }

    public float getC() {
        return this.c;
    }

    public float getM() {
        return this.m;
    }

    public float getY() {
        return this.y;
    }

    public float getK() {
        return this.k;
    }

    public CMYKColor cmyk() {
        return new CMYKColor(c, m, y, k);
    }
}