package de.perfectpattern.print.imposition.service.imposition;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.type.Priority;
import de.perfectpattern.print.imposition.model.type.Side;
import de.perfectpattern.print.imposition.service.imposition.layout.label.*;
import de.perfectpattern.print.imposition.util.FontUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static de.perfectpattern.print.imposition.util.DimensionUtil.dtp2mm;
import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManualLabelTest {

	@Test
	public void generateLabels() throws Exception {

		// arrange
		Sheet sheet = new Sheet.Builder().layoutTaskId("9C1D4A88-A294-4C5F-BF96-B651C37869FE").build();

		List<AbstractLabel> labels = new ArrayList<>();

		labels.add(new PullMarkProLabel());
		labels.add(new LayMarkLabel());
		labels.add(new PullMarkLabel());
		labels.add(new DebugLabel(sheet));
		labels.add(new PriorityLabel(Priority.Overnight));
		labels.add(new SheetSizeLabel(5000, 4000));
		labels.add(new BarcodeLabel("5BC3DE"));
		labels.add(new SideLabel());
		labels.add(new AmountLabel(100000));
		labels.add(new SheetIdLabel("5BC3DE"));

		// act
		byte[] result = writePdf(labels);

		// assert
		showPdfResult(result);

	}

	/**
	 * Helper class for displaying labeles.
	 *
	 * @param labels The list of labels to be displayed.
	 * @return The labels as pdf byte array.
	 */
	private byte[] writePdf(List<AbstractLabel> labels) throws Exception {

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Document doc = new Document(PageSize.A4);

		PdfWriter pdfWriter = PdfWriter.getInstance(doc, bos);
		doc.open();

		// act
		PdfContentByte cb = pdfWriter.getDirectContent();

		// for each label
		for (AbstractLabel label : labels) {
			doc.newPage();

			cb.setCMYKColorFillF(0, 0, 0, 0.1f);
			cb.rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight());
			cb.fill();

			cb.beginText();
			cb.setCMYKColorFillF(0, 0, 0, 1);
			cb.setFontAndSize(FontUtil.FONT_REGULAR, 20);
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, label.getClass().getSimpleName(), mm2dtp(20), 790, 0);
			cb.endText();

			for (int i = 20; i >= 0; i--) {
				float distanceBorder = mm2dtp(i);
				PdfTemplate template = label.createLabel(cb, Side.Front, distanceBorder);

				float posX = i > 12 ? mm2dtp(20) : mm2dtp(120);
				float posY = i > 12 ? mm2dtp(30) * (i - 12) : mm2dtp(20) * i + mm2dtp(20);

				cb.addTemplate(template, posX, posY - template.getHeight() + distanceBorder);

				cb.setCMYKColorStrokeF(0, 0, 0, 1);
				cb.setLineWidth(0.5f);
				cb.rectangle(posX, posY, template.getWidth(), distanceBorder);
				cb.stroke();

				cb.beginText();
				cb.setCMYKColorFillF(0, 0, 0, 1);
				cb.setFontAndSize(FontUtil.FONT_REGULAR, 10);
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, String.format("%.1f mm", dtp2mm(distanceBorder)), posX + template.getWidth() + mm2dtp(5), posY, 90);
				cb.endText();
			}
		}

		doc.close();

		return bos.toByteArray();
	}

	/**
	 * Helper class for showing a PDF Document.
	 *
	 * @param result The PDF Result as byte stream.
	 */
	private void showPdfResult(byte[] result) throws Exception {
		Path path = Files.createTempFile("pdf-label-", ".pdf");
		Files.write(path, result);

		String command = "evince " + path.toAbsolutePath();
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();

		Files.delete(path);
	}
}
