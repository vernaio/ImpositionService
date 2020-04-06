package de.perfectpattern.print.imposition.service.identification;

import com.lowagie.text.DocumentException;
import de.perfectpattern.print.imposition.model.Sheet;

public interface IdentificationService {

    /**
     * Generation of a Identification PDF.
     * @param sheet The Sheet details.
     * @param thumb The thumb as byte array.
     * @return The identification pdf as byte arrayy
     */
    byte[] generate(Sheet sheet, byte[] thumb) throws DocumentException, Exception;
}
