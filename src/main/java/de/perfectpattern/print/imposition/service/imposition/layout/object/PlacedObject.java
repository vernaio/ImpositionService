package de.perfectpattern.print.imposition.service.imposition.layout.object;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * The PlacedObject object. The concept is taken from XJDF PlacedObject.
 */
public abstract class PlacedObject {

    /**
     * Default constructor.
     */
    public PlacedObject() {
    }

    /**
     * Write the current object into the PdfWriter output.
     * @param pdfWriter The target pdf writer.
     */
    public abstract void write(PdfWriter pdfWriter) throws IOException, DocumentException, URISyntaxException, Exception;

}
