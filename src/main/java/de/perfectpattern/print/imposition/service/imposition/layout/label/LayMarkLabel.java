package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Side;
import de.perfectpattern.print.imposition.util.FontUtil;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

public class LayMarkLabel extends AbstractLabel {

	/**
	 * Default constructor.
	 */
	public 	LayMarkLabel() {
		super(mm2dtp(15));
	}

	@Override
	public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
		PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge);

		float labelHeight = distanceEdge > getMaxHeight() ? getMaxHeight() : distanceEdge;

		// draw lay
		template.setColorFill(Color.BLACK.cmyk());

		template.rectangle(mm2dtp(1),mm2dtp(1), getWidth() - mm2dtp(2), mm2dtp(1));
		template.fill();

		if(Side.Front == side) {

			if (distanceEdge > mm2dtp(2f)) {
				template.rectangle(getWidth() - mm2dtp(2), mm2dtp(1), mm2dtp(1), labelHeight - mm2dtp(1));
				template.fill();
			}

			// text
			if (distanceEdge > mm2dtp(5.5f)) {
				template.beginText();
				template.setCMYKColorFillF(0, 0, 0, 1);
				template.setFontAndSize(FontUtil.FONT_REGULAR, 6);
				template.showTextAligned(PdfContentByte.ALIGN_LEFT, "Anlage", mm2dtp(1.5f), mm2dtp(3), 0);
				template.endText();
			}

		} else {

			if (distanceEdge > mm2dtp(2f)) {
				template.rectangle(mm2dtp(1), mm2dtp(1), mm2dtp(1), labelHeight - mm2dtp(1));
				template.fill();
			}

			// text
			if (distanceEdge > mm2dtp(5.5f)) {
				template.beginText();
				template.setCMYKColorFillF(0, 0, 0, 1);
				template.setFontAndSize(FontUtil.FONT_REGULAR, 6);
				template.showTextAligned(PdfContentByte.ALIGN_LEFT, "Anlage", mm2dtp(2.5f), mm2dtp(3), 0);
				template.endText();
			}

		}

		// return template
		return template;
	}

}
