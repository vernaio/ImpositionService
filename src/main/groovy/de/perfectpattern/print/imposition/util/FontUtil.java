package de.perfectpattern.print.imposition.util;

import com.lowagie.text.pdf.BaseFont;
import de.perfectpattern.print.imposition.service.imposition.layout.label.AbstractLabel;
import org.apache.commons.io.IOUtils;

public class FontUtil {

    public static final BaseFont FONT_REGULAR = initFont();

    /**
     * Init regular base font.
     *
     * @return The regular base font.
     */
    private static BaseFont initFont() {
        BaseFont bf;

        try {
            byte[] fontBytes = IOUtils.toByteArray(
                    AbstractLabel.class.getResourceAsStream("/de/perfectpattern/print/imposition/sniglet-regular.ttf")
            );

            bf = BaseFont.createFont("sniglet-regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);

        } catch (Exception ex) {
            bf = null;
        }

        return bf;
    }
}
