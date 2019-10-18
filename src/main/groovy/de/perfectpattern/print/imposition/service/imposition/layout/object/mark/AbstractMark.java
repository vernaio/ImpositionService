package de.perfectpattern.print.imposition.service.imposition.layout.object.mark;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import de.perfectpattern.print.imposition.service.imposition.layout.object.PlacedObject;


public abstract class AbstractMark extends PlacedObject {

    private final Layer layer;

    /**
     * The marks layer.
     */
    protected enum Layer {
        LAYER_UNDER,
        CONTENT_LAYER
    }

    /**
     * Default constructor.
     */
    AbstractMark() {
        this(Layer.CONTENT_LAYER);
    }

    /**
     * Custom constructor. Accepting a layer where to put the mark.
     *
     * @param layer The layer.
     */
    AbstractMark(Layer layer) {
        this.layer = layer;
    }

    @Override
    public final void write(PdfWriter pdfWriter) throws Exception {
        PdfContentByte cb;

        if (Layer.LAYER_UNDER == layer) {
            cb = pdfWriter.getDirectContentUnder();
        } else {
            cb = pdfWriter.getDirectContent();
        }

        // write mark (!! state safe !!)
        cb.saveState();
        writeMark(cb);
        cb.restoreState();
    }

    /**
     * Write a mark in the appropriate PDFContentByte object.
     * @param cb The PDFContentByte object.
     */
    protected abstract void writeMark(PdfContentByte cb) throws Exception;
}
