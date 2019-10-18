package de.perfectpattern.print.imposition.service.imposition.layout.label;

import de.perfectpattern.print.imposition.util.DimensionUtil;

/**
 * Label to display the target amount of a sheet.
 */
public class AmountLabel extends CaptionLabel {

    public AmountLabel(int amount) {
        super(
                DimensionUtil.mm2dtp(50),
                "Auflage",
                String.format("%,d", amount)
        );
    }

}
