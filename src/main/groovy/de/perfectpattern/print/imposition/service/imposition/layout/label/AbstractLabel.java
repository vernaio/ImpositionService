package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import de.perfectpattern.print.imposition.model.type.Anchor;
import de.perfectpattern.print.imposition.model.type.Matrix;
import de.perfectpattern.print.imposition.model.type.Side;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

/**
 * This is an label class.
 */
public abstract class AbstractLabel {

    private static final float MAX_HEIGHT = mm2dtp(8);

    private final float width;


    /**
     * Custom constructor. Accepting a width for initializing.
     *
     * @param width The width in dtp plints.
     */
    AbstractLabel(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void placeLabel(PdfWriter pdfWriter, Side side, Anchor anchor, float offsetX, float offsetY, float distanceEdge) {
        final PdfContentByte cb = pdfWriter.getDirectContent();

        // save state
        cb.saveState();

        // create label
        PdfTemplate tplLabel = createLabel(cb, side, distanceEdge);


        Matrix ctm;

        if(Anchor.TopLeft == anchor || Anchor.BottomLeft == anchor) {
            ctm = new Matrix(0, -1, 1, 0, distanceEdge - tplLabel.getHeight(), tplLabel.getWidth());
        } else {
            ctm = new Matrix(0,1,-1,0, tplLabel.getHeight(), 0);
        }

        // place label
        cb.addTemplate(
                tplLabel,
                ctm.getA(),
                ctm.getB(),
                ctm.getC(),
                ctm.getD(),
                ctm.getE() + offsetX,
                ctm.getF() + offsetY
        );

        // restore state
        cb.restoreState();
    }

    /**
     * Getter returning the maximal height of a label.
     * @return The max height of a label.
     */
    protected float getMaxHeight() {
        return MAX_HEIGHT;
    }

    /**
     * Create the label.
     *
     * @param cb    The PdfContentByte object.
     * @param distanceEdge The distance to the sheet edge.
     * @param side The side of the surface
     * @return Label as template.
     */
    public abstract PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge);

}
