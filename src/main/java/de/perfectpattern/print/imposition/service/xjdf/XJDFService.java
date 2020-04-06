package de.perfectpattern.print.imposition.service.xjdf;

import de.perfectpattern.print.imposition.model.Sheet;

/**
 * Interface encapsulating the XJDF logic.
 */
public interface XJDFService {

    /**
     * Creates and returns a XJDF Package containing all artifacts.
     * @param sheet The sheet details.
     * @param artwork The artwork file.
     * @param thumb The thumb of the artwork.
     * @param identification The identification document of the artwork.
     * @return The XJDF Package as ZIP archive.
     */
    byte[] createXJDF(Sheet sheet, byte[] artwork, byte[] thumb, byte[] identification, byte[] ppf) throws Exception;
}
