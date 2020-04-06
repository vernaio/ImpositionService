package de.perfectpattern.print.imposition.service.importer.specific;

import de.perfectpattern.print.imposition.model.*;
import de.perfectpattern.print.imposition.model.type.*;
import de.perfectpattern.print.imposition.util.DimensionUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SprintOneV3ImporterTest {

    private final static String RES_ROOT = "/de/perfectpattern/print/imposition/service/importer/";

    private SprintOneV3Importer importer = new SprintOneV3Importer();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(importer, "sheetBleedMm", "20");
    }

    //@Test
    public void acceptDocument_true_1() throws IOException {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-1.xml"));

        // act
        boolean result = importer.acceptDocument(bytes);

        // assert
        assertTrue("Result is wrong.", result);
    }

    @Test
    public void acceptDocument_true_2() throws IOException {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-2.xml"));

        // act
        boolean result = importer.acceptDocument(bytes);

        // assert
        assertTrue("Result is wrong.", result);
    }

    @Test
    public void acceptDocument_false_1() throws IOException {

        // arrange
        byte[] bytes = new byte[0];

        // act
        boolean result = importer.acceptDocument(bytes);

        // assert
        assertFalse("Result is wrong.", result);
    }

    @Test
    public void acceptDocument_false_2() throws IOException {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "layout-1.xjdf"));

        // act
        boolean result = importer.acceptDocument(bytes);

        // assert
        assertFalse("Result is wrong.", result);
    }

    //@Test(expected = NumberFormatException.class)
    public void read_0() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-1.xml"));
        ReflectionTestUtils.setField(importer, "sheetBleedMm", "xx");

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
    }

    //@Test(expected = NullPointerException.class)
    public void read_00() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-1.xml"));
        ReflectionTestUtils.setField(importer, "sheetBleedMm", null);

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
    }

    //@Test
    public void read_1() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-1.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertNotNull("Sheet is null.", sheet);
        assertEquals("SheetId is wrong.", "0001-0D30", sheet.getSheetId());
        assertNull("LayoutTaskId is wrong.", sheet.getLayoutTaskId());
        assertEquals("Bleed sheet is wrong.", 56.692f, sheet.getBleed(), 0.001);
        assertEquals("WorkStyle is wrong.", WorkStyle.WorkAndBack, sheet.getWorkStyle());
        assertEquals("SurfaceContentsBox llx is wrong.", 0, sheet.getSurfaceContentsBox().getLlx(), 0.001f);
        assertEquals("SurfaceContentsBox lly is wrong.", 0, sheet.getSurfaceContentsBox().getLly(), 0.001f);
        assertEquals("SurfaceContentsBox urx is wrong.", 2494.488f, sheet.getSurfaceContentsBox().getUrx(), 0.001f);
        assertEquals("SurfaceContentsBox ury is wrong.", 1785.826, sheet.getSurfaceContentsBox().getUry(), 0.001f);
        assertEquals("Number CutBoxes is wrong.", 8, sheet.getCuttingParams().size());
        assertEquals("Number Positions is wrong.", 8, sheet.getPositions().size());

        Position position = sheet.getPositions().get(0);
        assertEquals("Position 0 llx is wrong.", 1247.244, position.getAbsoluteBox().getLlx(), 0.001);
        assertEquals("Position 0 lly is wrong.", 24.094, position.getAbsoluteBox().getLly(), 0.001);
        assertEquals("Position 0 urx is wrong.", 1859.527, position.getAbsoluteBox().getUrx(), 0.001);
        assertEquals("Position 0 ury is wrong.", 880.1576, position.getAbsoluteBox().getUry(), 0.001);
        assertEquals("Position 0 Orientation is wrong.", Orientation.Rotate90, position.getOrientation());

        CutBlock cutBlock = sheet.getCuttingParams().get(0);
        assertEquals("Box 0 llx is wrong.", 1247.244, cutBlock.getBox().getLlx(), 0.001);
        assertEquals("Box 0 lly is wrong.", 24.094, cutBlock.getBox().getLly(), 0.001);
        assertEquals("Box 0 urx is wrong.", 1859.527, cutBlock.getBox().getUrx(), 0.001);
        assertEquals("Box 0 ury is wrong.", 880.1576, cutBlock.getBox().getUry(), 0.001);
        assertEquals("BlockName is wrong.", "BLOCK-0", cutBlock.getBlockName());

    }

    @Test
    public void read_2() throws Exception {

        // arrange
        ReflectionTestUtils.setField(importer, "sheetBleedMm", "0");

        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-2.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertEquals("SheetId is wrong.", "0001-5BC3", sheet.getSheetId());
        assertNull("LayoutTaskId is wrong.", sheet.getLayoutTaskId());
        assertEquals("Bleed sheet is wrong.", 0f, sheet.getBleed(), 0.001);
        assertEquals("Amount is wrong.", 1500, sheet.getAmount());
        assertEquals("Number Positions is wrong.", 8, sheet.getPositions().size());
        assertEquals("Number CutBoxes is wrong.", 8, sheet.getCuttingParams().size());

        CutBlock cutBlock = sheet.getCuttingParams().get(7);
        assertEquals("Box 7 llx is wrong.", 22.677, cutBlock.getBox().getLlx(), 0.001);
        assertEquals("Box 7 lly is wrong.", 24.094, cutBlock.getBox().getLly(), 0.001);
        assertEquals("Box 7 urx is wrong.", 634.961, cutBlock.getBox().getUrx(), 0.001);
        assertEquals("Box 7 ury is wrong.", 880.1576, cutBlock.getBox().getUry(), 0.001);
        assertEquals("BlockName is wrong.", "BLOCK-7", cutBlock.getBlockName());

        Position pos;
        BinderySignature bs;
        SignatureCell sc;

        pos = sheet.getPositions().get(0);
        assertEquals("Llx is wrong.", 1247.244f, pos.getAbsoluteBox().getLlx(), 0.001f);
        assertEquals("Lly is wrong.", 905.669f, pos.getAbsoluteBox().getLly(), 0.001f);
        assertEquals("Urx is wrong.", 1859.527f, pos.getAbsoluteBox().getUrx(), 0.001f);
        assertEquals("Ury is wrong.", 1761.732f, pos.getAbsoluteBox().getUry(), 0.001f);
        assertEquals("Orientation is wrong.", Orientation.Rotate90, pos.getOrientation());

        bs = pos.getBinderySignature();
        assertEquals("FoldCatalog is wrong.", FoldCatalog.F4_1, bs.getFoldCatalog());
        assertEquals("Label is wrong.", "590359-10382_[12-13+18-19]", bs.getLabel());
        assertEquals("Amount is wrong.", 1500, bs.getAmount());
        assertEquals("JobID is wrong.", "590359-10382", bs.getJobId());
        assertEquals("Number SignatureCells is wrong.", 2, bs.getSignatureCells().size());
        assertFalse("Flipped is wrong.", bs.isFlipped());
        assertEquals("Total number of BS is wrong.", new Integer(8), bs.getBsNumberTotal());
        assertEquals("Current number of is wrong.", new Integer(7), bs.getBsNumberCurrent());

        sc = bs.getSignatureCells().get(0);
        assertEquals("Trim Size Width is wrong.", 394.355f, sc.getTrimSize().getX(), 0.001f);
        assertEquals("Trim Size Height is wrong.", 595.275f, sc.getTrimSize().getY(), 0.001f);
        assertEquals("Trim Spine is wrong.", 0f, sc.getTrimSpine(), 0.001f);
        assertEquals("Trim Head is wrong.", 8.503f, sc.getTrimHead(), 0.001f);
        assertEquals("Trim Face is wrong.", 33.675f, sc.getTrimFace(), 0.001f);
        assertEquals("Trim Foot is wrong.", 8.503f, sc.getTrimFoot(), 0.001f);
        assertEquals("Bleed Spine is wrong.", 0f, sc.getBleedSpine(), 0.001f);
        assertEquals("Bleed Head is wrong.", 8.503f, sc.getBleedHead(), 0.001f);
        assertEquals("Bleed Face is wrong.", 8.503f, sc.getBleedFace(), 0.001f);
        assertEquals("Bleed Foot is wrong.", 8.503f, sc.getBleedFoot(), 0.001f);
        assertEquals("Bleed Foot is wrong.", 8.503f, sc.getBleedFoot(), 0.001f);
        assertEquals("Orientation is wrong.", Orientation.Rotate0, sc.getOrientation());
        assertEquals("PageIndex Front is wrong.", 13, sc.getPageIndexFront());
        assertEquals("PageIndex Back is wrong.", 12, sc.getPageIndexBack());

        assertEquals("File Path Front is wrong.", "590359-10382.pdf", sc.getPageFront().getFile().toString());
        assertEquals("PageIndex Front is wrong.", 13, sc.getPageFront().getPage());
        assertEquals("File Path Back is wrong.", "590359-10382.pdf", sc.getPageBack().getFile().toString());
        assertEquals("PageIndex Back is wrong.", 12, sc.getPageBack().getPage());
    }

    @Test
    public void read_3() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-3.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertEquals("LayoutTaskId is wrong.", "E0FC3C1C-94A7-4D23-9181-E36BD6C916FA", sheet.getLayoutTaskId());

        Position pos = sheet.getPositions().get(0);
        assertEquals("AbsoluteBox llx is wrong.", mm2dtp(440f), pos.getAbsoluteBox().getLlx(), 0.001f);
        assertEquals("AbsoluteBox lly is wrong.", mm2dtp(7.75f), pos.getAbsoluteBox().getLly(), 0.001f);
        assertEquals("AbsoluteBox urx is wrong.", mm2dtp(653f), pos.getAbsoluteBox().getUrx(), 0.001f);
        assertEquals("AbsoluteBox ury is wrong.", mm2dtp(161.25f), pos.getAbsoluteBox().getUry(), 0.001f);

        assertEquals("Orientation is wrong.", Orientation.Rotate90, pos.getOrientation());
        assertEquals("AbsoluteBox width is wrong.", mm2dtp(213f), pos.getAbsoluteBox().getWidth(), 0.001f);
        assertEquals("AbsoluteBox height is wrong.", mm2dtp(153.5f), pos.getAbsoluteBox().getHeight(), 0.001f);
        assertEquals("BinderySignatureSize is wrong.", new XYPair(mm2dtp(153.5f), mm2dtp(213f)), pos.getBinderySignature().getBinderySignatureSize());

        assertEquals("FoldCatalog is wrong.", FoldCatalog.F2_1, pos.getBinderySignature().getFoldCatalog());
        assertEquals("Number of SignatureCells is wrong.", 1, pos.getBinderySignature().getSignatureCells().size());
        assertEquals("Priority is wrong.", Priority.Standard, pos.getBinderySignature().getPriority());
        SignatureCell sc = pos.getBinderySignature().getSignatureCells().get(0);

        assertEquals("BS TrimFace is wrong.", mm2dtp(4f), sc.getTrimFace(), 0.001f);
        assertEquals("BS TrimHead is wrong.", mm2dtp(1.5f), sc.getTrimHead(), 0.001f);
        assertEquals("BS TrimSpine is wrong.", mm2dtp(1.5f), sc.getTrimSpine(), 0.001f);
        assertEquals("BS TrimFoot is wrong.", mm2dtp(1.5f), sc.getTrimFoot(), 0.001f);

        assertEquals("TrimSize Width is wrong.", mm2dtp(148f), sc.getTrimSize().getX(), 0.001f);
        assertEquals("TrimSize Height is wrong.", mm2dtp(210), sc.getTrimSize().getY(), 0.001f);

        assertEquals("Probe sizes Horizontal", pos.getAbsoluteBox().getWidth(), sc.getTrimSize().getY() + sc.getTrimFoot() + sc.getTrimHead(), 0.001f);
        assertEquals("Probe sizes Vertical", pos.getAbsoluteBox().getHeight(), sc.getTrimSize().getX() + sc.getTrimFace() + sc.getTrimSpine(), 0.001f);
    }

    @Test
    public void read_4() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-4.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertNull("LayoutTaskId is wrong.", sheet.getLayoutTaskId());
        assertEquals("Priority is wrong.", Priority.Express, sheet.getPriority());
    }


    @Test
    public void read_5() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-5.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertEquals("LayoutTaskId is wrong.", "9C1D4A88-A294-4C5F-BF96-B651C37869FE", sheet.getLayoutTaskId());
        assertEquals("Priority is wrong.", "0100-36C2", sheet.getSheetId());
        assertEquals("Priority is wrong.", Priority.Standard, sheet.getPriority());
    }

    @Test
    public void read_6() throws Exception {

        // arrange
        byte[] bytes = IOUtils.toByteArray(SprintOneV3ImporterTest.class.getResource(RES_ROOT + "event-6.xml"));

        // act
        Sheet sheet = importer.importDocument(bytes);

        // assert
        printSheet(sheet);

        assertNotNull("RunList TwoSided Front is null.", sheet.getPositions().get(0).getBinderySignature().getSignatureCells().get(0).getPageFront());
        assertNotNull("RunList TwoSided Back is null.", sheet.getPositions().get(0).getBinderySignature().getSignatureCells().get(0).getPageBack());
        assertNotNull("RunList OneSided Front is null.", sheet.getPositions().get(1).getBinderySignature().getSignatureCells().get(0).getPageFront());
        assertNull("RunList OneSided Back is null.", sheet.getPositions().get(1).getBinderySignature().getSignatureCells().get(0).getPageBack());
    }

    /**
     * Helper method for debugging.
     *
     * @param sheet The sheet to be printed.
     */
    private void printSheet(Sheet sheet) {

        try {
            System.out.println("---------------------------------");
            System.out.println("SheetId: " + sheet.getSheetId());
            System.out.println("LayoutTaskId: " + sheet.getLayoutTaskId());
            System.out.println("Sheet Priority:" + sheet.getPriority());
            System.out.println("Amount: " + sheet.getAmount());
            System.out.println("SurfaceContentsBox: " + toMm(sheet.getSurfaceContentsBox().getSize()));
            System.out.println("Number CutBlocks  : " + sheet.getCuttingParams().size());

            for (int iCut = 0; iCut < sheet.getPositions().size(); iCut++) {
                CutBlock cutBlock = sheet.getCuttingParams().get(iCut);
                System.out.println("  Position " + iCut + ": BlockName: " + cutBlock.getBlockName());
                System.out.println("              Box: " + toMm(cutBlock.getBox()));
            }

            System.out.println("Number Positions  : " + sheet.getPositions().size());
            for (int iPos = 0; iPos < sheet.getPositions().size(); iPos++) {
                Position position = sheet.getPositions().get(iPos);
                System.out.println("  Position " + iPos + ": AbsoluteBox: " + toMm(position.getAbsoluteBox()));
                System.out.println("              Orientation: " + position.getOrientation());

                if (position.getBinderySignature() == null) {
                    System.out.println("              BinderySignatureSize: NOT DEFINED");
                } else {
                    System.out.println("              FoldCatalog: " + position.getBinderySignature().getFoldCatalog().getName());
                    System.out.println("              Priority: " + position.getBinderySignature().getPriority());
                    System.out.println("              BSNumberTotal: " + position.getBinderySignature().getBsNumberTotal());
                    System.out.println("              BSNumberCurrent: " + position.getBinderySignature().getBsNumberCurrent());

                    System.out.println("              BinderySignatureSize: " + toMm(position.getBinderySignature().getBinderySignatureSize()));
                    System.out.println("              Label: " + position.getBinderySignature().getLabel());

                    for (int iCell = 0; iCell < position.getBinderySignature().getSignatureCells().size(); iCell++) {
                        SignatureCell cell = position.getBinderySignature().getSignatureCells().get(iCell);
                        System.out.println("              - Signature Cell  " + iCell + ":");
                        System.out.println("                  Trim Size: " + toMm(cell.getTrimSize()));
                        System.out.println("                  Trim: " + toMm(cell.getTrimSpine(), cell.getTrimHead(), cell.getTrimFace(), cell.getTrimFoot()));
                        System.out.println("                  Bleed: " + toMm(cell.getBleedSpine(), cell.getBleedHead(), cell.getBleedFace(), cell.getBleedFoot()));
                        System.out.println("                  Orientation: " + cell.getOrientation());
                        System.out.println("                  PageIndex FB: " + cell.getPageIndexFront() + " / " + cell.getPageIndexBack());
                        System.out.println("                  Page Front: " + cell.getPageFront().getFile().toString() + " - Page: " + cell.getPageFront().getPage());
                        System.out.println("                  Page Back: " + cell.getPageBack().getFile().toString() + " - Page: " + cell.getPageBack().getPage());
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("");
            System.out.println("  INFO: EXCEPTION DURING VISUAL CONSOLE OUTPUT");
        }

        System.out.println("");
    }

    private static String toMm(float value) {
        return String.format("%.1fmm", DimensionUtil.dtp2mm(value));
    }

    public static String toMm(float... values) {
        StringBuilder result = new StringBuilder(50);

        for (float value : values) {
            result.append(" ").append(toMm(value));
        }

        return result.toString();
    }

    private static String toMm(XYPair value) {
        return String.format("%s x %s", toMm(value.getX()), toMm(value.getY()));
    }

    private static String toMm(Rectangle value) {
        return String.format("%s %s %s %s (width: %s, height: %s)", toMm(value.getLlx()), toMm(value.getLly()), toMm(value.getUrx()), toMm(value.getUry()), toMm(value.getWidth()), toMm(value.getHeight()));
    }
}