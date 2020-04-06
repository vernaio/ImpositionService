package de.perfectpattern.print.imposition.service.ppf;

import de.perfectpattern.print.imposition.model.CutBlock;
import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit test case for PPFServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class PPFServiceImplTest {

    private static final String RES_PPF_1 = "/de/perfectpattern/print/imposition/service/ppf/sheet-1.ppf";
    private static final String RES_PPF_2 = "/de/perfectpattern/print/imposition/service/ppf/sheet-2.ppf";

    @InjectMocks
    private PPFServiceImpl ppfService;

    @Ignore
    @Test
    /**
     * Old test without modification
     */
    public void createPPF_1() throws Exception {

        // arrange
        byte[] expected = IOUtils.toByteArray(PPFServiceImplTest.class.getResourceAsStream(RES_PPF_1));

        List<CutBlock> cuttingParams = new ArrayList<>();

        cuttingParams.add(new CutBlock.Builder()
                .blockName("FA-2421")
                .box(new Rectangle(25.4722f, 45.4737f, 1714.92f, 2432.25f))
                .build()
        );

        cuttingParams.add(new CutBlock.Builder()
                .blockName("FA-2422")
                .box(new Rectangle(1715.26f, 45.47f, 3404.71f, 2432.25f))
                .build()
        );

        Sheet sheet = new Sheet.Builder()
                .sheetId("6642986701")
                .surfaceContentsBox(new Rectangle(0, 0, 3430.26f, 2452.21f))
                .cuttingParams(cuttingParams)
                .build();

        // act
        byte[] result = ppfService.createPPF(sheet);

        // assert
        System.out.print(new String(result));

        assertEquals("Result is wrong.", new String(expected), new String(result));
    }

    @Test
    public void createPPF_2() throws Exception {

        // arrange
        byte[] expected = IOUtils.toByteArray(PPFServiceImplTest.class.getResourceAsStream(RES_PPF_2));

        List<CutBlock> cuttingParams = new ArrayList<>();

        cuttingParams.add(new CutBlock.Builder()
                .blockName("FA-2421")
                .box(new Rectangle(10f, 20f, 30f, 45f))
                .build()
        );

        Sheet sheet = new Sheet.Builder()
                .sheetId("6642986701")
                .surfaceContentsBox(new Rectangle(0, 0, 200f, 300f))
                .cuttingParams(cuttingParams)
                .build();

        
        // act
        byte[] result = ppfService.createPPF(sheet);

        // assert
        System.out.print(new String(result));

        // added replaceAll method to have linux newlines instead of windows newlines
        assertEquals("Result is wrong.", new String(expected), new String(result).replaceAll("\\r\\n", "\n"));
    }
}