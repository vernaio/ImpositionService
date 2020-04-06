package de.perfectpattern.print.imposition.service.imposition.layout.object.mark;

import com.lowagie.text.pdf.PdfContentByte;
import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Rectangle;

/**
 * The implementation of a box mark.
 */
public class BoxMark extends AbstractMark {

    private final Rectangle box;

    private final Color color;

    /**
     * Custom constructor.
     *
     * @param box The box to be placed.
     */
    public BoxMark(Rectangle box) {
        this(box, Color.MAGENTA);
    }

    /**
     * Custom constructor.
     *
     * @param box The box to be placed.
     * @param color The color of the box.
     */
    public BoxMark(Rectangle box, Color color) {
        this.box = box;
        this.color = color;
    }

    @Override
    public void writeMark(PdfContentByte cb) {

        // line settings
        cb.setCMYKColorStrokeF(
                color.getC(),
                color.getM(),
                color.getY(),
                color.getK()
        );

        cb.setLineWidth(1);

        // write rectangle
        cb.rectangle(
                box.getLlx(),
                box.getLly(),
                box.getWidth(),
                box.getHeight()
        );

        // stroke
        cb.stroke();
    }
}
