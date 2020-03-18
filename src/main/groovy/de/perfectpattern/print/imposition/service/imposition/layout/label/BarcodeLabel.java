package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;

import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Side;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

public class BarcodeLabel extends AbstractLabel {

    private static final float PADDING_SIDE = mm2dtp(4);
    private static final float PADDING_BOTTOM = mm2dtp(1);
    private static final float PADDING_TOP= mm2dtp(1);

    private final String text;

    /**
     * Custom constructor. Accepting a width for initializing.
     *
     * @param text The barcode text.
     */
    public BarcodeLabel(String text) {
        super(mm2dtp(60));

        this.text = text;
    }

    @Override
    public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
        PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge);

        float labelHeight = distanceEdge > getMaxHeight() ? getMaxHeight() : distanceEdge;


        Barcode128 barcode = new Barcode128();
        barcode.setCode(text);
        barcode.setBaseline(0);
        barcode.setAltText("");

        if(labelHeight > mm2dtp(4)) {
            barcode.setBarHeight(labelHeight - PADDING_BOTTOM - PADDING_TOP);
        } else {
            barcode.setBarHeight(labelHeight - PADDING_BOTTOM - 0.25f * PADDING_TOP);
        }

        Image imgBarcode = barcode.createImageWithBarcode(cb, Color.BLACK.cmyk(), Color.WHITE.cmyk());
        imgBarcode.setAbsolutePosition(PADDING_SIDE, PADDING_BOTTOM);
        imgBarcode.scaleAbsoluteWidth(getWidth() - 2 * PADDING_SIDE);
        // added try catch block to work with spo api fat jar
        try {
        	template.addImage(imgBarcode);
        } catch (DocumentException e) {
        	System.out.println("Error in BarcodeLabel class, template.addImage");
        }


        return template;
    }
}
