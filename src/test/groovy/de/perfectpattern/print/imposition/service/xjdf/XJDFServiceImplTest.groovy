package de.perfectpattern.print.imposition.service.xjdf;

import org.apache.commons.io.FilenameUtils
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream

import static junit.framework.Assert.assertTrue
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull;

/**
 * JUnit test case for XJDFService.
 */
@RunWith(MockitoJUnitRunner.class)
public class XJDFServiceImplTest {

    @InjectMocks
    private XJDFServiceImpl xjdfService;

    @Test
    public void createXJDF() throws Exception {

        // arrange
        byte[] artwork = "artwork".getBytes()
        byte[] thumb = "thumb".getBytes()
        byte[] identification = "identification".getBytes()

        // act
        byte[] bytesZip = xjdfService.createXJDF(null, artwork, thumb, identification)

        // assert
        byte[] bytesXjdf = extractXJDF(bytesZip)

        assertNotNull("No XJDF Document has been found.", bytesXjdf)
        assertTrue("XJDF Document is empty.", bytesXjdf.size() > 0)
        System.out.println(new String(bytesXjdf))

        def xjdf = new XmlSlurper().parse(new ByteArrayInputStream(bytesXjdf))

        assertEquals("JobID is wrong.", "JOB_ID", xjdf.@JobID.toString())
        assertEquals("Types is wrong.", "ConventionalPrinting Cutting", xjdf.@Types.toString())

        assertEquals("ThumbNail FileSpec is wrong.", "preview/thumb.jpg", xjdf.ResourceSet.Resource[0].Preview.FileSpec.@URL.toString())
        assertEquals("Part ThumbNail FileSpec is wrong.", "ThumbNail", xjdf.ResourceSet.Resource[0].Part.@PreviewType.toString())

        assertEquals("Identification FileSpec is wrong.", "preview/identification.pdf", xjdf.ResourceSet.Resource[1].Preview.FileSpec.@URL.toString())
        assertEquals("Part Identification FileSpec is wrong.", "Identification", xjdf.ResourceSet.Resource[1].Part.@PreviewType.toString())

        assertEquals("RunList FileSpec is wrong.", "runlist/artwork.pdf", xjdf.ResourceSet.Resource.RunList.FileSpec.@URL.toString())
    }

    /**
     * Extract xjdf from zip stream.
     * @param zip The zip file as byte array.
     * @return The xjdf document as byte array.
     */
    private static byte[] extractXJDF(byte[] zip) throws Exception {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip))
        ZipEntry ze = zis.getNextEntry()
        byte[] buffer = new byte[1024]
        byte[] xjdf = null

        while(ze != null && xjdf == null) {
            if("xjdf".equalsIgnoreCase(FilenameUtils.getExtension(ze.getName()))) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream()
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    bos.write(buffer, 0, len)
                }
                bos.close()

                xjdf = bos.toByteArray()
            }

            ze = zis.getNextEntry()
        }

        return xjdf
    }
}