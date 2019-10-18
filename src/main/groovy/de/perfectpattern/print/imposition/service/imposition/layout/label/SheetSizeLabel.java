package de.perfectpattern.print.imposition.service.imposition.layout.label;


import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.util.DimensionUtil;

import static de.perfectpattern.print.imposition.util.DimensionUtil.dtp2cm;

/**
 * The sheet size label showing the size of the sheet.
 */
public class SheetSizeLabel extends CaptionLabel {

    public SheetSizeLabel(Rectangle rectangle) {
        this(rectangle.getWidth(), rectangle.getHeight());
    }

    public SheetSizeLabel(float width, float height) {
        super(
                DimensionUtil.mm2dtp(80),
                "Format",
                formatSize(width, height)
        );
    }

    public static String formatSize(float width, float height) {

        return String.format(
                "%.1f x %.1f cm",
                dtp2cm(width),
                dtp2cm(height)
        );

    }
}
