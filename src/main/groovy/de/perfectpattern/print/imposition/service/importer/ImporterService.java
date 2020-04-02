package de.perfectpattern.print.imposition.service.importer;

import java.io.IOException;

import de.perfectpattern.print.imposition.model.Sheet;

/**
 * This service imports multiple file formats.
 */
public interface ImporterService {

    /**
     * Read an Document as byte array.
     * @param bytes The document as object object.
     * @return The Sheet object object.
     * @throws Exception 
     * @throws IOException 
     */
    Sheet importDocument(byte[] bytes) throws IOException, Exception;
}
