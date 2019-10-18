package de.perfectpattern.print.imposition.service.importer.specific;

import de.perfectpattern.print.imposition.model.Sheet;

/**
 * Interface encapsulates the logic to importDocument an XJDF document.
 */
public interface Importer {

    /**
     * Returns true, in case the byte array can be processed by this importer.
     * @param bytes The bytes to be analysed.
     * @return True in case document can be processed by the importer - otherwise false.
     */
    boolean acceptDocument(byte[] bytes);

    /**
     * Read an Document as byte array.
     * @param bytes The document as object object.
     * @return The Sheet object object.
     */
    Sheet importDocument(byte[] bytes);
}
