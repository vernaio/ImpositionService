package de.perfectpattern.print.imposition.service.imposition;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.service.importer.specific.Importer;
import de.perfectpattern.print.imposition.service.imposition.layout.LayoutProcessor;
import de.perfectpattern.print.imposition.service.thumb.ThumbService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "data.root=/")
public class ImpositionServiceTest {

    private static final String RES_ROOT = "/de/perfectpattern/print/imposition/service/imposition/";

    private static final Path FILE_ROOT = FileUtils.getTempDirectory().toPath().resolve("imposition-test");

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean manualCheck = false;

    @Rule
    public TestName name = new TestName();

    @Autowired
    private ImpositionService impositionService;

    @Autowired
    private LayoutProcessor layoutProcessor;

    @Autowired
    private ThumbService thumbService;

    @Autowired
    @Qualifier("sprintOneV3Importer")
    private Importer spoV3Importer;


    public void setManualCheck(boolean manualCheck) {
        this.manualCheck = manualCheck;
    }

    @BeforeClass
    public static void init() throws Exception {
        // clean up
        FileUtils.deleteDirectory(FILE_ROOT.toFile());
        Files.createDirectories(FILE_ROOT);

        // log instruction
        Map<String, String> env = System.getenv();

        System.out.println("");
        System.out.println("---");
        System.out.println("");
        System.out.println("PDF Comparision TEST:");
        System.out.println("=====================");
        System.out.println("Results can be found here: " + env.get("HOSTNAME") + ":" + FILE_ROOT.toString());
        System.out.println("");
        System.out.println("Docker Command:");
        System.out.println("docker cp " + env.get("HOSTNAME") + ":" + FILE_ROOT.toString() + " ./");
        System.out.println("");
        System.out.println("---");
        System.out.println("");
    }

    @Test
    public void checkManualCheck() {
        assertFalse("ManualCheck must be set to false.", manualCheck);
    }

    /**
     * Test of single paged products.
     *
     * @throws Exception
     */
    @Test
    public void imposition_form_1() throws Exception {
        genericFormTest("form-1");
    }

    /**
     * Generic test method for forms.
     *
     * @param formName The forms name.
     */
    private void genericFormTest(String formName) throws Exception {

        // arrange
        byte[] xml = getModifiedEventXml(formName);
        Sheet sheet = spoV3Importer.importDocument(xml);

        // act
        long startTime = System.currentTimeMillis();
        byte[] pdf = impositionService.impose(sheet);
        long totalDuration = System.currentTimeMillis() - startTime;

        // assert
        System.out.println("Total Duration (" + formName + "): " + totalDuration + " ms");

        byte[] bytesReference_p1 = IOUtils.toByteArray(ImpositionServiceTest.class.getResource(RES_ROOT + formName + "/page-1-reference.jpg").toURI());
        byte[] bytesReference_p2 = IOUtils.toByteArray(ImpositionServiceTest.class.getResource(RES_ROOT + formName + "/page-2-reference.jpg").toURI());

        assertPdf(pdf, bytesReference_p1, bytesReference_p2);
    }

    /**
     * Helper method for getting a modified Event XML.
     *
     * @param formName The name of the form.
     * @return The modified xml.
     */
    private byte[] getModifiedEventXml(String formName) throws Exception {
        byte[] xml = IOUtils.toByteArray(ImpositionServiceTest.class.getResource(RES_ROOT + formName + "/event.xml").toURI());
        Path dir = Paths.get(ImpositionServiceTest.class.getResource(RES_ROOT + formName).toURI());

        List<Path> files = Files.list(dir).collect(Collectors.toList());

        for (Path file : files) {
            String fileName = file.getFileName().toString();

            if (fileName.startsWith("job_")) {
                xml = new String(xml).replaceAll(fileName.substring("job_".length()), file.toString()).getBytes();
            }
        }

        return xml;
    }

    /**
     * Asserts the result of a pdf against a jpeg reference.
     *
     * @param pdf       The pdf to be asserted
     * @param jpgRef_p1 Page one of the reference image
     * @param jpgRef_p2 Page one of the reference image
     */
    private void assertPdf(byte[] pdf, byte[] jpgRef_p1, byte[] jpgRef_p2) throws Exception {

        if (manualCheck) {
            showPdfResult(pdf);
            return;
        }

        // auto test
        final int difference_1, difference_2;
        long startTime = System.currentTimeMillis();

        // prepare files
        Path dir = FILE_ROOT.resolve(name.getMethodName());
        Files.createDirectories(dir);
        Path pdfOriginal = dir.resolve("original.pdf");
        Files.write(pdfOriginal, pdf);

        // process page 1
        Path pathReference_p1 = dir.resolve("page-1-reference.jpg");
        Files.write(pathReference_p1, jpgRef_p1);

        Path pathGenerated_p1 = dir.resolve("page-1-generated.jpg");
        imConvert(pdfOriginal, pathGenerated_p1, 0);

        Path pathDiff_p1 = dir.resolve("page-1-DIFF.png");
        difference_1 = imCompare(pathGenerated_p1, pathReference_p1, pathDiff_p1);

        // process page 2
        if (jpgRef_p2 != null) {
            Path pathReference_p2 = dir.resolve("page-2-reference.jpg");
            Files.write(pathReference_p2, jpgRef_p2);

            Path pathGenerated_p2 = dir.resolve("page-2-generated.jpg");
            imConvert(pdfOriginal, pathGenerated_p2, 1);

            Path pathDiff_p2 = dir.resolve("page-2-DIFF.png");
            difference_2 = imCompare(pathGenerated_p2, pathReference_p2, pathDiff_p2);
        } else {
            difference_2 = -1;
        }

        // write result
        String text = "";
        text += "Report:\n";
        text += "=======\n";
        text += "Difference Page 1: " + difference_1 + "\n";
        text += "Difference Page 2: " + difference_2 + "\n";
        text += "\n";
        text += "Duration: " + (System.currentTimeMillis() - startTime) + " ms\n";
        text += "Timestamp: " + dateFormat.format(new Date()) + "\n";
        text += "\n";
        text += "\n";
        text += "ImageMagick Details:\n";
        text += "====================\n";
        text += imVersion();

        if (difference_1 > 0 || difference_2 > 0) {
            FileUtils.writeByteArrayToFile(dir.resolve("report (ERROR).txt").toFile(), text.getBytes());

            fail("PDF Diff check has failed. See: " + dir.toString());
        } else {
            FileUtils.writeByteArrayToFile(dir.resolve("report (OK).txt").toFile(), text.getBytes());
        }
    }

    /**
     * Helper method for converting PDFs.
     */
    private void imConvert(Path pathOriginal, Path pathTarget, int page) throws Exception {
        ProcessBuilder
                pb =
                new ProcessBuilder("convert", "-shave", "50x0", "-density", "150", "-quality", "100", pathOriginal.toString() + "[" + page + "]", pathTarget.toString());
        pb.start().waitFor();
    }


    /**
     * Helper method for comparing images.
     */
    private int imCompare(Path pathSrc, Path pathRef, Path pathDiff) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "compare",
                "-metric",
                "pae",
                pathSrc.toString(),
                pathRef.toString(),
                pathDiff.toString()
        );

        Process process = pb.redirectErrorStream(true).start();
        byte[] result = IOUtils.toByteArray(process.getInputStream());
        process.waitFor();

        String str = new String(result);
        str = str.substring(0, str.indexOf(" "));
        return Integer.parseInt(str);
    }

    /**
     * Helper method for getting ImageMagick version.
     */
    private String imVersion() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("compare", "--version");
        Process process = pb.redirectErrorStream(true).start();
        byte[] version = IOUtils.toByteArray(process.getInputStream());
        process.waitFor();

        return new String(version);
    }

    /**
     * Helper class for showing a PDF Document.
     *
     * @param result The PDF Result as byte stream.
     */
    private void showPdfResult(byte[] result) throws Exception {
        Path path = Files.createTempFile("pdf-comparision-", ".pdf");
        Files.write(path, result);

        String command = "evince " + path.toAbsolutePath();
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();

        Files.delete(path);
    }
}