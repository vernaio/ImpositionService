package de.perfectpattern.print.imposition.service.imposition.layout.object.mark;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import de.perfectpattern.print.imposition.model.BinderySignature;
import de.perfectpattern.print.imposition.model.type.*;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;
import static de.perfectpattern.print.imposition.util.FontUtil.FONT_REGULAR;

public class BinderySignatureInfoMark extends AbstractMark {

	private BinderySignature binderySignature;
	private Rectangle absoluteBox;
	private Orientation orientation;
	private Side side;

	/**
	 * Custom constructor accepting several parameter for initializing.
	 */
	public BinderySignatureInfoMark(Rectangle absoluteBox, Orientation orientation, BinderySignature binderySignature, Side side) {
		this.absoluteBox = absoluteBox;
		this.orientation = orientation;
		this.binderySignature = binderySignature;
		this.side = side;
	}

	@Override
	protected void writeMark(PdfContentByte cb) throws Exception {

		// define front / back
		Side side;

		if(Side.Front == this.side && !binderySignature.isFlipped() || Side.Back == this.side && binderySignature.isFlipped()) {
			side = Side.Front;
		} else if (Side.Back == this.side) {
			side = Side.Back;
		} else {
			throw new Exception("Side is not defined.");
		}

		// build text
		String text = String.format(" #%s    (%s)    %d  von  %d ", binderySignature.getJobId(), side.toString(), binderySignature.getBsNumberCurrent(), binderySignature.getBsNumberTotal());

		// geometry
		final float paddingFace = mm2dtp(5);
		final float fontSize = 5.5f;
		final float bsWidth = binderySignature.getBinderySignatureSize().getX();
		final float bsHeight = binderySignature.getBinderySignatureSize().getY();

		final float markHeight = mm2dtp(1.9f);
		final float markWidth = FONT_REGULAR.getWidthPoint(text, fontSize);

		// create mark
		PdfTemplate tpl = cb.createTemplate(bsWidth, bsHeight);

		// background
		tpl.setColorFill(Color.WHITE.cmyk());

		if(Side.Front == side) {
			tpl.rectangle(bsWidth - markWidth - paddingFace, 1, markWidth, markHeight * 0.75f - 1);
		} else {
			tpl.rectangle(paddingFace, 1, markWidth, markHeight * 0.75f - 1);
		}

		tpl.fill();

		// text
		tpl.beginText();
		tpl.setColorFill(Color.BLACK.cmyk());
		tpl.setFontAndSize(FONT_REGULAR, fontSize);

		if(Side.Front == side) {
			tpl.showTextAligned(PdfContentByte.ALIGN_RIGHT, text, bsWidth - paddingFace, 1, 0);
		} else {
			tpl.showTextAligned(PdfContentByte.ALIGN_LEFT, text, 0 + paddingFace, 1, 0);
		}

		tpl.endText();

		// place mark
		Matrix ctm = new Matrix(orientation, bsWidth, bsHeight);
		cb.addTemplate(tpl, ctm.getA(), ctm.getB(), ctm.getC(), ctm.getD(), ctm.getE() + absoluteBox.getLlx(), ctm.getF() + absoluteBox.getLly());
	}
}
