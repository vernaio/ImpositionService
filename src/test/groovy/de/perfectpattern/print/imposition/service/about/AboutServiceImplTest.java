package de.perfectpattern.print.imposition.service.about;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * JUnit test of the AboutServiceImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class AboutServiceImplTest {

    @InjectMocks
    private AboutServiceImpl aboutService;

    @Test
    public void getPdfLibrary() {

        // arrange

        // act
        final String result = aboutService.getPdfLibrary();

        // assert
        assertEquals("PDFEngine Version is wrong.", "OpenPDF 1.2.17", result);

    }

    @Test
    public void getAppName() {

        // arrange

        // act
        final String result = aboutService.getAppName();

        // assert
        System.out.println(result);
    }
}