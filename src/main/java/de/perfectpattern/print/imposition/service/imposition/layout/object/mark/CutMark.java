package de.perfectpattern.print.imposition.service.imposition.layout.object.mark;

import com.lowagie.text.pdf.PdfContentByte;
import de.perfectpattern.print.imposition.util.DimensionUtil;

public class CutMark extends AbstractMark {

    private static final float MARK_LENGTH = DimensionUtil.mm2dtp(7);

    private static final float MARK_DISTANCE = DimensionUtil.mm2dtp(2);

    private final Type type;

    private final float posX;

    private final float posY;

    public enum Type {
        LowerLeft,
        UpperLeft,
        LowerRight,
        UpperRight
    }

    /**
     * Custom constructor.
     * @param type The cut marks type.
     * @param posX The X position.
     * @param posY The Y position.
     */
    public CutMark(Type type, float posX, float posY) {
        super(AbstractMark.Layer.LAYER_UNDER);

        this.type = type;
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void writeMark(PdfContentByte cb) {

        // line settings
        cb.setCMYKColorStrokeF(1, 1, 1, 1);
        cb.setLineWidth(0.5f);

        // draw corner marks
        if(Type.UpperLeft == type || Type.UpperRight == type) {
            // vertical lines top
            cb.moveTo(posX, posY + MARK_DISTANCE);
            cb.lineTo(posX, posY + MARK_LENGTH);

        } else {
            // vertical lines bottom
            cb.moveTo(posX, posY - MARK_DISTANCE);
            cb.lineTo(posX, posY - MARK_LENGTH);
        }

        if(Type.UpperLeft == type || Type.LowerLeft == type) {
            // horizontal lines left
            cb.moveTo(posX - MARK_DISTANCE, posY);
            cb.lineTo(posX - MARK_LENGTH, posY);

        } else {
            // horizontal lines right
            cb.moveTo(posX + MARK_DISTANCE, posY);
            cb.lineTo(posX + MARK_LENGTH, posY);
        }

        // stroke
        cb.stroke();
    }
}
