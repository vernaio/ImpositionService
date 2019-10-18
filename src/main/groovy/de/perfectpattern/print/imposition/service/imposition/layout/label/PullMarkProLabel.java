package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Side;
import de.perfectpattern.print.imposition.util.FontUtil;

import static de.perfectpattern.print.imposition.util.DimensionUtil.dtp2mm;
import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

public class PullMarkProLabel extends AbstractLabel {

	private static final float TICK_HEIGHT = mm2dtp(2);

	private static final float SECTION_WIDTH = mm2dtp(5f);
	private static final float STEP_HEIGHT = mm2dtp(0.5f);

	private static final float NUMBER_STEPS = 4;
	private static final float HORIZONTAL_PADDING = mm2dtp(3);

	/**
	 * Default constructor.
	 */
	public PullMarkProLabel() {
		super((2 * NUMBER_STEPS + 2) * SECTION_WIDTH + 2 * HORIZONTAL_PADDING);
	}

	@Override
	public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
		final float markBleed = NUMBER_STEPS * STEP_HEIGHT;

		PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge + markBleed);

		// no back
		if(Side.Back == side) {
			return template;
		}

		// center block
		template.setColorFill(Color.GRAY.cmyk());
		template.rectangle(NUMBER_STEPS * SECTION_WIDTH + HORIZONTAL_PADDING,0, SECTION_WIDTH * 2, 2 * markBleed + TICK_HEIGHT);
		template.fill();

		// ticks
		template.setLineWidth(2f);

		float posX, posY;

		template.beginText();

		for(int i = 0; i < NUMBER_STEPS + 1; i ++) {
			if(i % 2 == 0) {
				template.setColorStroke(Color.BLACK.cmyk());
				template.setFontAndSize(FontUtil.FONT_REGULAR, 8);
				template.setColorFill(Color.BLACK.cmyk());
			} else {
				template.setColorStroke(Color.GRAY.cmyk());
				template.setFontAndSize(FontUtil.FONT_REGULAR, 6);
				template.setColorFill(Color.GRAY.cmyk());
			}

			// tick left
			posX = i * SECTION_WIDTH + HORIZONTAL_PADDING;
			posY = 2 * markBleed + TICK_HEIGHT;
			template.moveTo(posX, 0);
			template.lineTo(posX, posY);

			if (distanceEdge > mm2dtp(6.9f)) {
				template.showTextAligned(
					PdfContentByte.ALIGN_CENTER,
					String.format("%.1f", dtp2mm((NUMBER_STEPS - i) * STEP_HEIGHT)),
					posX, posY + mm2dtp(1), 0);
			}

			// tick right
			posX = getWidth() - i * SECTION_WIDTH - HORIZONTAL_PADDING;
			template.moveTo(posX, 0);
			template.lineTo(posX, 2 * markBleed + TICK_HEIGHT);

			if (distanceEdge > mm2dtp(6.9f)) {
				template.showTextAligned(
					PdfContentByte.ALIGN_CENTER,
					String.format("%.1f", dtp2mm((NUMBER_STEPS - i) * STEP_HEIGHT)),
					posX, posY + mm2dtp(1), 0);
			}

			template.stroke();
		}

		template.endText();
		template.stroke();

		// line
		template.setLineWidth(1.5f);
		template.moveTo(HORIZONTAL_PADDING, 0);

		template.lineTo(NUMBER_STEPS * SECTION_WIDTH + HORIZONTAL_PADDING, NUMBER_STEPS * STEP_HEIGHT);
		template.lineTo((NUMBER_STEPS + 2) * SECTION_WIDTH + HORIZONTAL_PADDING, NUMBER_STEPS * STEP_HEIGHT);
		template.lineTo((2 * NUMBER_STEPS + 2) * SECTION_WIDTH + HORIZONTAL_PADDING, NUMBER_STEPS * STEP_HEIGHT * 2);

		template.stroke();


		// return template
		return template;
	}


}
