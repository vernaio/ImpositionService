package de.perfectpattern.print.imposition.service.xjdf;

import de.perfectpattern.print.imposition.model.Sheet;
import org.apache.commons.io.FileUtils;
import org.cip4.lib.xjdf.builder.XJdfBuilder;
import org.cip4.lib.xjdf.schema.*;
import org.cip4.lib.xjdf.type.URI;
import org.cip4.lib.xjdf.xml.XJdfPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;


@Controller
public class XJDFServiceImpl implements XJDFService {

    private static final Logger log = LoggerFactory.getLogger(XJDFServiceImpl.class);

    /**
     * Default constructor.
     */
    public XJDFServiceImpl() {
    }

    @Override
    public byte[] createXJDF(Sheet sheet, byte[] artwork, byte[] thumb, byte[] identification) throws Exception {
        Path workDir = Files.createTempDirectory("xjdf-imposition");
        log.info("Create temp dir: " + workDir.toString());

        ByteArrayOutputStream bos;

        try {
            // copy files to temporarily location
            Path pathArtwork = workDir.resolve("artwork.pdf");
            Files.write(pathArtwork, artwork);

            Path pathThumb = workDir.resolve("thumb.jpg");
            Files.write(pathThumb, thumb);

            Path pathIdentification = workDir.resolve("identification.pdf");
            Files.write(pathIdentification, identification);

            // create xjdf document
            XJdfBuilder builder = new XJdfBuilder("JOB_ID");
            builder.getXJdf().getTypes().add("ConventionalPrinting");
            builder.getXJdf().getTypes().add("Cutting");

            // add resources
            addPreviewThumb(builder, pathThumb);
            addPreviewIdentification(builder, pathIdentification);
            addRunList(builder, pathArtwork);

            // create output
            bos = new ByteArrayOutputStream();
            XJdfPackager packager = new XJdfPackager(bos);
            packager.packageXjdf(builder.getXJdf());

        } catch (Exception ex) {
            log.error("Error creating the XJDF Document.", ex);
            throw ex;

        } finally {
            FileUtils.deleteDirectory(workDir.toFile());
        }

        return bos.toByteArray();
    }


    /**
     * Append the Preview Identiication Resource to the XJDF.
     *
     * @param builder  The XJDF Builder object.
     * @param pathFile The path to the artwork.
     */
    private void addPreviewIdentification(XJdfBuilder builder, Path pathFile) throws URISyntaxException {
        FileSpec fileSpec = new FileSpec();
        fileSpec.setURL(
                new URI(
                        pathFile.toUri(),
                        "preview/identification.pdf"
                )
        );

        Part part = new Part();
        part.setPreviewType(Part.PreviewType.IDENTIFICATION);

        Preview preview = new Preview();
        preview.setFileSpec(fileSpec);

        builder.addResource(preview, part);
    }

    /**
     * Append the Preview Thumb Resource to the XJDF.
     *
     * @param builder  The XJDF Builder object.
     * @param pathFile The path to the artwork.
     */
    private void addPreviewThumb(XJdfBuilder builder, Path pathFile) throws URISyntaxException {
        FileSpec fileSpec = new FileSpec();
        fileSpec.setURL(
                new URI(
                        pathFile.toUri(),
                        "preview/thumb.jpg"
                )
        );

        Part part = new Part();
        part.setPreviewType(Part.PreviewType.THUMB_NAIL);

        Preview preview = new Preview();
        preview.setFileSpec(fileSpec);

        builder.addResource(preview, part);
    }

    /**
     * Append the RunList Resource to the XJDF.
     *
     * @param builder  The XJDF Builder object.
     * @param pathFile The path to the artwork.
     */
    private void addRunList(XJdfBuilder builder, Path pathFile) throws URISyntaxException {
        FileSpec fileSpec = new FileSpec();
        fileSpec.setURL(
                new URI(
                        pathFile.toUri(),
                        "runlist/artwork.pdf"
                )
        );

        RunList runList = new RunList();
        runList.setFileSpec(fileSpec);

        builder.addResource(runList);
    }
}
