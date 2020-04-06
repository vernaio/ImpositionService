package de.perfectpattern.print.imposition.service.imposition;

import de.perfectpattern.print.imposition.model.Sheet;

/**
 * Interface of a ImpositionService class.
 */
public interface ImpositionService {

    /**
     * Imposition of a sheet PDF.
     * @param sheet The sheet details.
     * @return The PDF document as byte array.
     */
    byte[] impose(Sheet sheet) throws Exception;

}
