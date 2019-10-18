package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Priority;
import de.perfectpattern.print.imposition.model.type.Side;
import de.perfectpattern.print.imposition.util.DimensionUtil;

/**
 * The priority label shows the sheets priority.
 */
public class PriorityLabel extends CaptionLabel {

    private final Priority priority;

    private static final float BLEED_LABEL = DimensionUtil.mm2dtp(1);

    /**
     * Custom constructor. Accepting a Priority for initializing.
     * @param priority The sheets priority.
     */
    public PriorityLabel(Priority priority) {
        super(
                DimensionUtil.mm2dtp(50),
                "Priorität", priority.toString()
        );

        this.priority = priority;
    }

    @Override
    public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
        PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge + BLEED_LABEL);

        // define background color
        CMYKColor bgColor;

        if(Priority.Standard == priority) {
            bgColor = Color.STANDARD.cmyk();

        } else if (Priority.Express == priority) {
            bgColor = Color.EXPRESS.cmyk();

        } else if (Priority.Overnight == priority) {
            bgColor = Color.OVERNIGHT.cmyk();

        } else {
            bgColor = Color.WHITE.cmyk();
        }

        // set colored rectangle
        float labelHeight = distanceEdge > getMaxHeight() ? getMaxHeight() : distanceEdge;

        template.setColorFill(bgColor);
        template.rectangle(0, 0, getWidth(), labelHeight + BLEED_LABEL);
        template.fill();

        // generate and add text layer
        PdfTemplate text = super.createLabel(cb, side, labelHeight);
        template.addTemplate(text, 0, BLEED_LABEL);

        return template;
    }
}
