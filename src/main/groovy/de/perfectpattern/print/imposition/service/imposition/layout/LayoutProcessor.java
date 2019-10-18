package de.perfectpattern.print.imposition.service.imposition.layout;

import de.perfectpattern.print.imposition.model.Sheet;

import java.io.IOException;

/**
 * Interface encapsulating the logic to compute a layout.
 */
public interface LayoutProcessor {

    /**
     * Computes a layout based on a sheet definition.
     * @param sheet The sheet definition.
     * @return The Layout definition.
     */
    Layout generateLayout(Sheet sheet) throws IOException;

}
