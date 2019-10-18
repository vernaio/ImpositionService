package de.perfectpattern.print.imposition.service.imposition.layout.label;


import de.perfectpattern.print.imposition.util.DimensionUtil;

/**
 * The side lable indecates whether the side is front or back.
 */
public class SideLabel extends CaptionLabel {

    public SideLabel() {
        super(
                DimensionUtil.mm2dtp(40),
                "Side", "Front", "Back"
        );
    }

}
