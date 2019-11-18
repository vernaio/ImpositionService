package de.perfectpattern.print.imposition.service.about;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * JUnit test of the AboutServiceImpl.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AboutServiceImplTest {

    @Autowired
    private AboutService aboutService;

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