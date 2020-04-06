package de.perfectpattern.print.imposition.service.imposition.layout.object.content;

import com.lowagie.text.pdf.*;
import de.perfectpattern.print.imposition.service.imposition.layout.object.PlacedObject;
import de.perfectpattern.print.imposition.model.type.Orientation;
import de.perfectpattern.print.imposition.model.type.Rectangle;

import java.io.IOException;


public class ContentObject extends PlacedObject {
    private final Rectangle clipBox;
    private final Rectangle trimBox;
    private final Orientation orientation;
    private final PdfReader pdfReader;
    private final int pageNumber;

    /**
     * Custom constructor.
     */
    public ContentObject(Rectangle clipBox, Rectangle trimBox, Orientation orientation, PdfReader pdfReader, int pageNumber) {
        this.clipBox = clipBox;
        this.trimBox = trimBox;
        this.orientation = orientation;
        this.pdfReader = pdfReader;
        this.pageNumber = pageNumber;
    }

    public Rectangle getClipBox() {
        return clipBox;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public PdfReader getPdfReader() {
        return pdfReader;
    }

    public float getPageNumber() {
        return pageNumber;
    }

    @Override
    public void write(PdfWriter pdfWriter) throws IOException {
        final PdfContentByte cb = pdfWriter.getDirectContent();

        // importDocument pdf page
        PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, pageNumber + 1);

        // read and NORMALIZE trimbox relative to the mediabox. !!
        float pdfTrimRight, pdfTrimTop, pdfTrimLeft, pdfTrimBottom, pdfTrimHeight, pdfTrimWidth;

        {
            com.lowagie.text.Rectangle pdfTrimBox = pdfReader.getBoxSize(pageNumber + 1, "trim");
            com.lowagie.text.Rectangle pdfMediaBox = pdfReader.getBoxSize(pageNumber + 1, "media");

            if(pdfTrimBox == null) {
                pdfTrimBox = pdfMediaBox;
            }

            // normalization regarding to the origin (0, 0)
            pdfTrimRight = pdfTrimBox.getRight() - pdfMediaBox.getLeft();
            pdfTrimTop = pdfTrimBox.getTop() - pdfMediaBox.getBottom();
            pdfTrimLeft = pdfTrimBox.getLeft() - pdfMediaBox.getLeft();
            pdfTrimBottom = pdfTrimBox.getBottom() - pdfMediaBox.getBottom();

            pdfTrimHeight = pdfTrimBox.getHeight();
            pdfTrimWidth = pdfTrimBox.getWidth();
        }

        // compute relative trimBox coordinates (trimbox here means LayoutTrimBox)
        float trimBoxLlx = trimBox.getLlx() - clipBox.getLlx();
        float trimBoxLly = trimBox.getLly() - clipBox.getLly();

        // positioning of the PDF page into the template
        PdfTemplate template = cb.createTemplate(clipBox.getWidth(), clipBox.getHeight());

        template.saveState(); // !! IMPORTANT: saveState() and restoreState() are absolutely necessary here !!
        template.setCMYKColorFillF(0,0,0,0); // white background
        template.rectangle(0, 0, clipBox.getWidth(), clipBox.getHeight());
        template.fill();
        template.restoreState();

        // pdf rotation (CTM Transformation)
        int pdfRotation = pdfReader.getPageRotation(pageNumber + 1);
        Orientation finalOrientation = Orientation.findByDegree(orientation.getDegree() - pdfRotation);

        if (Orientation.Rotate0 == finalOrientation) {
            float scalePdfHeight= trimBox.getHeight() / pdfTrimHeight;
            float scalePdfWidth = trimBox.getWidth() / pdfTrimWidth;

            template.addTemplate(
                    page,
                    1 * scalePdfWidth, 0, 0, 1 * scalePdfHeight,
                    trimBoxLlx - pdfTrimLeft * scalePdfWidth,
                    trimBoxLly - pdfTrimBottom * scalePdfHeight
            );

        } else if (Orientation.Rotate90 == finalOrientation) {
            float scalePdfWidth = trimBox.getHeight() / pdfTrimWidth;
            float scalePdfHeight = trimBox.getWidth() / pdfTrimHeight;

            template.addTemplate(
                    page,
                    0, 1 * scalePdfWidth, -1 * scalePdfHeight, 0,
                    trimBoxLlx + pdfTrimTop * scalePdfHeight,
                    trimBoxLly - pdfTrimLeft * scalePdfWidth
            );

        } else if (Orientation.Rotate180 == finalOrientation) {
            float scalePdfHeight = trimBox.getHeight() / pdfTrimHeight;
            float scalePdfWidth = trimBox.getWidth() / pdfTrimWidth;

            template.addTemplate(
                    page,
                    -1 * scalePdfWidth, 0, 0, -1 * scalePdfHeight,
                    trimBoxLlx + pdfTrimRight * scalePdfWidth,
                    trimBoxLly + pdfTrimTop * scalePdfHeight
            );

        } else if (Orientation.Rotate270 == finalOrientation) {
            float scalePdfWidth = trimBox.getHeight() / pdfTrimWidth;
            float scalePdfHeight = trimBox.getWidth() / pdfTrimHeight;

            template.addTemplate(page,
                    0, -1 * scalePdfWidth, 1 * scalePdfHeight, 0,
                    trimBoxLlx - pdfTrimBottom * scalePdfHeight,
                    trimBoxLly + pdfTrimRight * scalePdfWidth
            );

        } else {
            throw new IOException("The following orientation is not supported: " + finalOrientation.toString());
        }

        // place template on the sheet
        cb.addTemplate(template, clipBox.getLlx(), clipBox.getLly());
    }

    @Override
    public String toString() {
        return "ContentObject{" +
                "clipBox=" + clipBox +
                ", trimBox=" + trimBox +
                ", orientation=" + orientation +
                ", pdfReader=" + pdfReader.toString() +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
