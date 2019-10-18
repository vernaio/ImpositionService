package de.perfectpattern.print.imposition.service.importer;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.service.importer.specific.Importer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit test case for ImportetrServiceImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImporterServiceImplTest {

    @Mock
    private Importer importerMock_1;

    @Mock
    private Importer importerMock_2;

    @Mock
    private Importer importerMock_3;

    @Spy
    private List<Importer> importers = new ArrayList<>(3);

    @InjectMocks
    private ImporterServiceImpl importerService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        importers.add(importerMock_1);
        importers.add(importerMock_2);
        importers.add(importerMock_3);
    }

    @Test
    public void importDocument() {

        // arrange
        byte[] bytes = "My Document".getBytes();
        Sheet sheet = new Sheet.Builder().amount(42).build();

        doReturn(false).when(importerMock_1).acceptDocument(bytes);
        doReturn(true).when(importerMock_2).acceptDocument(bytes);

        doReturn(sheet).when(importerMock_2).importDocument(bytes);

        // act
        Sheet result = importerService.importDocument(bytes);

        // assert
        assertEquals("Result is wrong.", sheet, result);

        verify(importerMock_1, times(1)).acceptDocument(any());
        verify(importerMock_2, times(1)).acceptDocument(bytes);
        verify(importerMock_3, times(0)).acceptDocument(any());

        verify(importerMock_1, times(0)).importDocument(any());
        verify(importerMock_2, times(1)).importDocument(bytes);
        verify(importerMock_3, times(0)).importDocument(any());
    }
}