package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Side;


public class SpaceLabel extends AbstractLabel {

    /**
     * Custom constructor. Accepting a width value for initializing.
     * @param width The fixed (minimal) width of the label.
     */
    public SpaceLabel(float width) {
        this(width, 0);
    }

    /**
     * Custom constructor. Accepting two values for initializing.
     * @param width The fixed (minimal) width of the label.
     * @param dynamicWidth The dynamic variable width part of the label.
     */
    public SpaceLabel(float width, float dynamicWidth) {
        super(dynamicWidth > 1 ? width + System.currentTimeMillis() % dynamicWidth : width);
    }


    @Override
    public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
        return cb.createTemplate(getWidth(), distanceEdge);
    }
}
