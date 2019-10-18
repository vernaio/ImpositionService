package de.perfectpattern.print.imposition.service.identification;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.*;
import de.perfectpattern.print.imposition.model.Position;
import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.SignatureCell;
import de.perfectpattern.print.imposition.model.type.*;
import de.perfectpattern.print.imposition.service.imposition.layout.label.*;
import de.perfectpattern.print.imposition.util.FontUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static de.perfectpattern.print.imposition.model.type.Color.*;
import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

/**
 * Implementation of the Identification Processor Interface. This class contains the logic to generate a
 * identification PDF.
 */
@Controller
public class IdentificationServiceImpl implements IdentificationService {

    @Override
    public byte[] generate(Sheet sheet, byte[] thumb) throws Exception {

        // create pdf
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A3.rotate());

        PdfWriter pdfWriter = PdfWriter.getInstance(doc, bos);
        doc.open();

        // set meta data
        doc.addTitle("Title");
        doc.addAuthor("SheetImposition");
        doc.addSubject("Identification PDF");
        doc.addKeywords("Identification");
        doc.addCreator("SprintOne");



        // create direct content
        PdfContentByte cb = pdfWriter.getDirectContent();

        // thumb as background
        Image image = Image.getInstance(thumb);

        float imgScaleX = doc.getPageSize().getWidth() / (image.getWidth() - 2 * sheet.getBleed());
        float imgScaleY = doc.getPageSize().getHeight() / (image.getHeight() - 2 * sheet.getBleed());

        image.setAbsolutePosition(-sheet.getBleed() * imgScaleX, -sheet.getBleed() * imgScaleY);
        image.scalePercent(imgScaleX * 100f, imgScaleY * 100f);
        cb.addImage(image);

        // scale factors
        float scaleX = doc.getPageSize().getWidth() / sheet.getSurfaceContentsBox().getWidth();
        float scaleY = doc.getPageSize().getHeight() / sheet.getSurfaceContentsBox().getHeight();

        // process positions
        for (Position position : sheet.getPositions()) {

            // display area box
            cb.setCMYKColorStrokeF(0, 1, 0, 0);
            cb.setLineWidth(3);
            cb.rectangle(
                    position.getAbsoluteBox().getLlx() * scaleX,
                    position.getAbsoluteBox().getLly() * scaleY,
                    position.getAbsoluteBox().getWidth() * scaleX,
                    position.getAbsoluteBox().getHeight() * scaleY
            );
            cb.stroke();

            // create info box
            PdfTemplate tplInfoBox = createInfoBox(pdfWriter, position);
            Matrix ctm = new Matrix(position.getOrientation(), tplInfoBox.getWidth(), tplInfoBox.getHeight());

            float boxWidth, boxHeight;

            if (Orientation.Rotate0 == position.getOrientation() || Orientation.Rotate180 == position.getOrientation()) {
                boxWidth = tplInfoBox.getWidth();
                boxHeight = tplInfoBox.getHeight();
            } else {
                boxWidth = tplInfoBox.getHeight();
                boxHeight = tplInfoBox.getWidth();
            }

            float posX = (position.getAbsoluteBox().getLlx() + position.getAbsoluteBox().getWidth() / 2) * scaleX - boxWidth / 2;
            float posY = (position.getAbsoluteBox().getLly() + position.getAbsoluteBox().getHeight() / 2) * scaleY - boxHeight / 2;

            cb.addTemplate(
                    tplInfoBox,
                    ctm.getA(),
                    ctm.getB(),
                    ctm.getC(),
                    ctm.getD(),
                    ctm.getE() + posX,
                    ctm.getF() + posY
            );
        }

        // main bar
        PdfTemplate tplMainBar = createMainBar(pdfWriter, sheet, doc.getPageSize().getWidth());
        cb.addTemplate(tplMainBar, 0, 0);

        // close document
        doc.close();

        // return pdf as byte array
        return bos.toByteArray();
    }

    /**
     * Create main bar.
     *
     * @return The main bar as PdfTemplate
     */
    private PdfTemplate createMainBar(PdfWriter pdfWriter, Sheet sheet, float pageWidth) {
        PdfContentByte cb = pdfWriter.getDirectContent();

        float barHeight = mm2dtp(10);
        float marginBottom = mm2dtp(2);

        // init bar
        PdfTemplate tpl = cb.createTemplate(pageWidth, barHeight);
        tpl.setColorFill(WHITE.cmyk());
        tpl.rectangle(0, 0, pageWidth, barHeight);
        tpl.fill();

        float offsetLeft = mm2dtp(5);
        float offsetRight = pageWidth - offsetLeft;

        // sheet id (left)
        PdfTemplate lblSheetId = new SheetIdLabel(sheet.getSheetId()).createLabel(cb, Side.Front, barHeight - marginBottom);
        tpl.addTemplate(lblSheetId, offsetLeft, marginBottom);
        offsetLeft += lblSheetId.getWidth();

        // priority (left)
        PdfTemplate lblPrio = new PriorityLabel(sheet.getPriority()).createLabel(cb, Side.Front, barHeight - marginBottom);
        tpl.addTemplate(lblPrio, offsetLeft, marginBottom);
        offsetLeft += lblPrio.getWidth();

        // amount (left)
        PdfTemplate lblAmount = new AmountLabel(sheet.getAmount()).createLabel(cb, Side.Front, barHeight - marginBottom);
        tpl.addTemplate(lblAmount, offsetLeft, marginBottom);
        offsetLeft += lblAmount.getWidth();

        // barcode (right)
        PdfTemplate lblBarcode = new BarcodeLabel(sheet.getSheetId()).createLabel(cb, Side.Front, barHeight - marginBottom);
        offsetRight -= lblBarcode.getWidth();
        tpl.addTemplate(lblBarcode, offsetRight, marginBottom);

        // sheet size
        PdfTemplate lblSheetSize = new SheetSizeLabel(sheet.getSurfaceContentsBox()).createLabel(cb, Side.Front, barHeight - marginBottom);
        offsetRight -= lblSheetSize.getWidth();
        tpl.addTemplate(lblSheetSize, offsetRight, marginBottom);

        // upper border line
        tpl.setLineWidth(2);
        tpl.setColorStroke(BLACK.cmyk());
        tpl.moveTo(0, barHeight);
        tpl.lineTo(pageWidth, barHeight);
        tpl.stroke();

        return tpl;
    }

    /**
     * Create the info box for a position.
     *
     * @param position The position details.
     * @return The info box as pdf template
     */
    private PdfTemplate createInfoBox(PdfWriter pdfWriter, Position position) throws DocumentException {
        final float WIDTH = mm2dtp(70);
        final float HEIGHT = mm2dtp(30);

        final float BARCODE_HEIGHT = mm2dtp(8);
        final float BARCODE_PADDING_H = mm2dtp(10);
        final float BARCODE_PADDING_V = mm2dtp(2);

        final float TEXT_HEIGHT = HEIGHT - BARCODE_HEIGHT - 2 * BARCODE_PADDING_V;


        PdfTemplate tpl = pdfWriter.getDirectContent().createTemplate(WIDTH, HEIGHT);


        // white background
        tpl.setColorFill(WHITE.cmyk());
        tpl.rectangle(0, 0, tpl.getWidth(), tpl.getHeight());
        tpl.fill();

        // priority box
        Color prioColor;

        if (Priority.Standard == position.getBinderySignature().getPriority()) {
            prioColor = STANDARD;

        } else if (Priority.Express == position.getBinderySignature().getPriority()) {
            prioColor = EXPRESS;

        } else if (Priority.Overnight == position.getBinderySignature().getPriority()) {
            prioColor = OVERNIGHT;

        } else {
            prioColor = WHITE;
        }

        tpl.setColorFill(prioColor.cmyk());
        tpl.rectangle(mm2dtp(1), mm2dtp(1), WIDTH - mm2dtp(2), TEXT_HEIGHT);
        tpl.fill();

        // barcode
        Barcode barcode = new Barcode128();
        barcode.setCode(position.getBinderySignature().getJobId());
        barcode.setBaseline(0);
        barcode.setBarHeight(BARCODE_HEIGHT);

        Image imgBarcode = barcode.createImageWithBarcode(tpl, BLACK.cmyk(), WHITE.cmyk());
        imgBarcode.setAbsolutePosition(BARCODE_PADDING_H, HEIGHT - BARCODE_HEIGHT - BARCODE_PADDING_V);
        imgBarcode.scaleAbsoluteWidth(WIDTH - 2 * BARCODE_PADDING_H);
        tpl.addImage(imgBarcode);


        tpl.setColorStroke(BLACK.cmyk());
        tpl.setColorFill(BLACK.cmyk());

        // black box
        tpl.setLineWidth(1);
        tpl.rectangle(mm2dtp(1), mm2dtp(1), WIDTH - mm2dtp(2), HEIGHT - mm2dtp(2));
        tpl.stroke();

        // set text
        tpl.beginText();

        // job id
        tpl.setFontAndSize(FontUtil.FONT_REGULAR, 18);
        tpl.showTextAligned(
                PdfContentByte.ALIGN_CENTER,
                position.getBinderySignature().getJobId(),
                WIDTH / 2, TEXT_HEIGHT - 14, 0);

        // pages
        tpl.setFontAndSize(FontUtil.FONT_REGULAR, 10);
        tpl.showTextAligned(
                PdfContentByte.ALIGN_CENTER,
                generatePageString(position),
                WIDTH / 2, TEXT_HEIGHT - 26, 0);

        // amount
        tpl.setFontAndSize(FontUtil.FONT_REGULAR, 9);
        tpl.showTextAligned(
                PdfContentByte.ALIGN_LEFT,
                String.format("Auflage:  %,d", position.getBinderySignature().getAmount()),
                mm2dtp(2), mm2dtp(2.5f), 0);

        // priority
        tpl.setFontAndSize(FontUtil.FONT_REGULAR, 9);
        tpl.showTextAligned(
                PdfContentByte.ALIGN_RIGHT,
                position.getBinderySignature().getPriority().toString().toUpperCase(),
                WIDTH - mm2dtp(2), mm2dtp(2.5f), 0);

        tpl.endText();

        // return info box
        return tpl;
    }

    private String generatePageString(Position position) {
        List<SignatureCell> cells = position.getBinderySignature().getSignatureCells();

        List<Integer> pagesFront = new ArrayList<>(cells.size());
        List<Integer> pagesBack = new ArrayList<>(cells.size());

        for(SignatureCell cell: cells) {
            pagesFront.add(cell.getPageIndexFront() + 1);
            pagesBack.add(cell.getPageIndexBack() + 1);
        }


        // build text
        String text = String.format(
                "Seiten:  %s  /  %s",
                StringUtils.join(pagesFront, " + "),
                StringUtils.join(pagesBack, " + ")
                );

        return text;
    }
}
