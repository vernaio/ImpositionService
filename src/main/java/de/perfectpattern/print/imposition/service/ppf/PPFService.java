package de.perfectpattern.print.imposition.service.ppf;

import de.perfectpattern.print.imposition.model.Sheet;

public interface PPFService {

    /**
     * Create a PPF for a sheet.
     * @param sheet The sheet details.
     * @return The PPF document as byte array.
     */
    byte[] createPPF(Sheet sheet) throws Exception;
}
