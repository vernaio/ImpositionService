package de.perfectpattern.print.imposition.model;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import org.junit.Test;

import static org.junit.Assert.*;

public class SheetTest {

	@Test(expected = IllegalArgumentException.class)
	public void buildValidation() {

		// arrange
		Sheet sheet = new Sheet.Builder()
			.surfaceContentsBox(new Rectangle(1,2,3,4))
			.bleed(5)
			.build();

		// act & assert
		sheet.getMediaBox();
	}

	@Test
	public void getMediaBox() {

		// arrange
		Sheet sheet = new Sheet.Builder()
			.surfaceContentsBox(new Rectangle(0,0,3,4))
			.bleed(5)
			.build();

		// act
		final Rectangle mediaBox = sheet.getMediaBox();

		// assert
		assertEquals("MediaBox Llx is wrong.", 0, mediaBox.getLlx(), 0.00001);
		assertEquals("MediaBox Lly is wrong.", 0, mediaBox.getLly(), 0.00001);
		assertEquals("MediaBox Urx is wrong.", 13, mediaBox.getUrx(), 0.00001);
		assertEquals("MediaBox Ury is wrong.", 14, mediaBox.getUry(), 0.00001);
	}

	@Test
	public void getTrimBox() {

		// arrange
		Sheet sheet = new Sheet.Builder()
			.surfaceContentsBox(new Rectangle(0,0,3,4))
			.bleed(5)
			.build();

		// act
		final Rectangle trimBox = sheet.getTrimBox();

		// assert
		assertEquals("TrimBox Llx is wrong.", 5, trimBox.getLlx(), 0.00001);
		assertEquals("TrimBox Lly is wrong.", 5, trimBox.getLly(), 0.00001);
		assertEquals("TrimBox Urx is wrong.", 8, trimBox.getUrx(), 0.00001);
		assertEquals("TrimBox Ury is wrong.", 9, trimBox.getUry(), 0.00001);
	}
}