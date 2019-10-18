package de.perfectpattern.print.imposition.service.identification;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.service.importer.specific.Importer;
import de.perfectpattern.print.imposition.service.importer.specific.SprintOneV3Importer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@RunWith(MockitoJUnitRunner.class)
public class ManualIdentificationServiceTest {

	private static final String RES_ROOT = "/de/perfectpattern/print/imposition/service/identification/";

	@InjectMocks
	private IdentificationService identificationService = new IdentificationServiceImpl();

	@Test
	public void identification_form_1() throws Exception {

		// arrange
		byte[] bytes = IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-1.jpg"));

		Importer importer = new SprintOneV3Importer();
		ReflectionTestUtils.setField(importer, "sheetBleedMm", "0");

		Sheet sheet = importer.importDocument(
			IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-1.xml"))
		);



		// act
		byte[] result = identificationService.generate(sheet, bytes);

		// assert
		showPdfResult(result);
	}

	@Test
	public void identification_form_2() throws Exception {

		// arrange
		byte[] bytes = IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-2.jpg"));

		Importer importer = new SprintOneV3Importer();
		ReflectionTestUtils.setField(importer, "sheetBleedMm", "0");

		Sheet sheet = importer.importDocument(
			IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-2.xml"))
		);

		// act
		byte[] result = identificationService.generate(sheet, bytes);

		// assert
		showPdfResult(result);
	}

	@Test
	public void identification_form_3() throws Exception {

		// arrange
		byte[] bytes = IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-3.jpg"));

		Importer importer = new SprintOneV3Importer();
		ReflectionTestUtils.setField(importer, "sheetBleedMm", "0");

		Sheet sheet = importer.importDocument(
			IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-3.xml"))
		);

		// act
		byte[] result = identificationService.generate(sheet, bytes);

		// assert
		showPdfResult(result);
	}

	@Test
	public void identification_form_3_bleed() throws Exception {

		// arrange
		byte[] bytes = IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-3-bleed.jpg"));

		Importer importer = new SprintOneV3Importer();
		ReflectionTestUtils.setField(importer, "sheetBleedMm", "20");

		Sheet sheet = importer.importDocument(
			IOUtils.toByteArray(ManualIdentificationServiceTest.class.getResourceAsStream(RES_ROOT + "form-3.xml"))
		);

		// act
		byte[] result = identificationService.generate(sheet, bytes);

		// assert
		showPdfResult(result);
	}

	/**
	 * Show the PDF Result.
	 *
	 * @param result The PDF Result as byte stream.
	 */
	private void showPdfResult(byte[] result) throws Exception {
		Path path = Files.createTempFile("identification-", ".pdf");
		Files.write(path, result);

		String command = "evince " + path.toAbsolutePath();
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();

		Files.delete(path);
	}
}
