package de.perfectpattern.print.imposition.service.imposition.layout.label;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.type.Color;
import de.perfectpattern.print.imposition.model.type.Side;
import de.perfectpattern.print.imposition.util.DimensionUtil;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

public class PullMarkLabel extends AbstractLabel {

	private static final float MARK_HEIGHT = DimensionUtil.mm2dtp(3f);

	private static final float CENTER_LINE_WIDTH = DimensionUtil.mm2dtp(0.3f);

	/**
	 * Default constructor.
	 */
	public PullMarkLabel() {
		super(mm2dtp(25));
	}

	@Override
	public PdfTemplate createLabel(PdfContentByte cb, Side side, float distanceEdge) {
		PdfTemplate template = cb.createTemplate(getWidth(), distanceEdge + MARK_HEIGHT / 2);

		// no back
		if(Side.Back == side) {
			return template;
		}

		final float segmentLength = getWidth() / 5;
		final float levelHeight = MARK_HEIGHT / 6;

		template.setColorFill(Color.BLACK.cmyk());

		// center line
		template.rectangle(0,MARK_HEIGHT / 2 - CENTER_LINE_WIDTH / 2, segmentLength * 5, CENTER_LINE_WIDTH);
		template.fill();

		if(Side.Front == side) {

			// first level
			template.rectangle(segmentLength, MARK_HEIGHT / 2 - levelHeight, segmentLength, levelHeight * 2);
			template.fill();

			// second level
			template.rectangle(segmentLength * 2, MARK_HEIGHT / 2 - 2 * levelHeight, segmentLength, levelHeight * 4);
			template.fill();

			// third level
			template.rectangle(segmentLength * 3, MARK_HEIGHT / 2 - 3 * levelHeight, segmentLength, levelHeight * 6);
			template.fill();

		} else {

			// first level
			template.rectangle(segmentLength * 3, MARK_HEIGHT / 2 - levelHeight, segmentLength, levelHeight * 2);
			template.fill();

			// second level
			template.rectangle(segmentLength * 2, MARK_HEIGHT / 2 - 2 * levelHeight, segmentLength, levelHeight * 4);
			template.fill();

			// third level
			template.rectangle(segmentLength, MARK_HEIGHT / 2 - 3 * levelHeight, segmentLength, levelHeight * 6);
			template.fill();

		}

		return template;
	}
}
