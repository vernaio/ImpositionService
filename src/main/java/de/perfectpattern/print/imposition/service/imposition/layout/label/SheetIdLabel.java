package de.perfectpattern.print.imposition.service.imposition.layout.label;

import de.perfectpattern.print.imposition.util.DimensionUtil;

/**
 * The sheet id label shows the sheet identifier.
 */
public class SheetIdLabel extends CaptionLabel {

    /**
     * Custom constructor. Accepting a sheet id for initializing.
     * @param sheetId The sheets identifier.
     */
    public SheetIdLabel(String sheetId) {
        super(
                DimensionUtil.mm2dtp(60),
                "Bogen-Nr", sheetId
        );
    }
}
