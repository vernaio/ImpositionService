package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Side;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;
import static de.perfectpattern.print.imposition.util.FontUtil.FONT_REGULAR;

public abstract class CaptionLabel extends AbstractLabel {

    private static final float PADDING_LEFT = mm2dtp(4);
    private static final float PADDING_BOTTOM = mm2dtp(1);

    private static final float FONT_SIZE_CAPTION = 8;

    private final String captionFront;
    private final String textFront;

    private final String captionBack;
    private final String textBack;

    /**
     * Custom constructor. Accepting several params for initializing.
     * @param width The width of the label.
     * @param caption the caption of the label.
     * @param text The text of the label.
     */
    public CaptionLabel(float width, String caption, String text) {
        this(width, caption, text, caption, text);
    }

    /**
     * Custom constructor. Accepting several params for initializing.
     * @param width The width of the label.
     * @param caption the caption of the label.
     * @param textFront The text of the front page.
     * @param textBack The text of the back page.
     */
    public CaptionLabel(float width, String caption, String textFront, String textBack) {
        this(width, caption, textFront, caption, textBack);
    }

    /**
     * Custom constructor. Accepting serveral params for initializing.
     * @param width The width of the label.
     * @param captionFront The caption of the front page.
     * @param textFront The text of the front page.
     * @param captionBack The caption of the back page.
     * @param textBack The text of the back page.
     */
    public CaptionLabel(float width, String captionFront, String textFront, String captionBack, String textBack) {
        super(width);

        this.captionFront = captionFront;
        this.textFront = textFront;
        this.captionBack = captionBack;
        this.textBack = textBack;
    }

    /**
     * Write the caption label.
     * @param cb The direct content object.
     * @param side The side the surface applies to.
     * @param distanceEdge The distance to the border.
     * @return Label as template.
     */
    @Override
    public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
        PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge);
        float height = distanceEdge > getMaxHeight() ? getMaxHeight() : distanceEdge;

        // text
        template.beginText();
        template.setCMYKColorFillF(0, 0, 0, 1);

        String caption = Side.Front == side ? captionFront : captionBack;
        String text = Side.Front == side ? textFront : textBack;
        caption += ":";

        if (height > mm2dtp(10)) {

            // caption
            template.setTextMatrix(PADDING_LEFT, height - FONT_SIZE_CAPTION);
            template.setFontAndSize(FONT_REGULAR, FONT_SIZE_CAPTION);
            template.showText(caption);

            // text
            template.setTextMatrix(PADDING_LEFT, PADDING_BOTTOM);
            template.setFontAndSize(FONT_REGULAR, (height - PADDING_BOTTOM - FONT_SIZE_CAPTION));
            template.showText(text);

        } else if (height - PADDING_BOTTOM > FONT_SIZE_CAPTION) {

            // caption
            template.setTextMatrix(PADDING_LEFT, PADDING_BOTTOM);
            template.setFontAndSize(FONT_REGULAR, FONT_SIZE_CAPTION);
            template.showText(caption);

            // text
            float offsetX = FONT_REGULAR.getWidthPoint(caption, FONT_SIZE_CAPTION) + mm2dtp(1);
            template.setTextMatrix(PADDING_LEFT + offsetX, PADDING_BOTTOM);
            template.setFontAndSize(FONT_REGULAR, (height - PADDING_BOTTOM));
            template.showText(text);

        } else if (height > PADDING_BOTTOM) {

            // caption
            final float captionFontSize = height - PADDING_BOTTOM;
            template.setTextMatrix(PADDING_LEFT, PADDING_BOTTOM);
            template.setFontAndSize(FONT_REGULAR, captionFontSize);
            template.showText(caption);

            // text
            float offsetX = FONT_REGULAR.getWidthPoint(caption, captionFontSize) + mm2dtp(1);
            template.setTextMatrix(PADDING_LEFT + offsetX, PADDING_BOTTOM);
            template.setFontAndSize(FONT_REGULAR, captionFontSize);
            template.showText(text);

        }

        template.endText();
        return template;
    }
}
