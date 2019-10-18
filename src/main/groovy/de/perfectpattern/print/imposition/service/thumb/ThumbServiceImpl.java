package de.perfectpattern.print.imposition.service.thumb;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class ThumbServiceImpl implements ThumbService {

    private static final Logger log = LoggerFactory.getLogger(ThumbServiceImpl.class);

    @Override
    public byte[] renderPdf(byte[] bytesPdf) throws IOException, InterruptedException {

        Path dir = Files.createTempDirectory("sheetImposition");

        Path pathPdf = dir.resolve("form.pdf");
        Path pathPng = dir.resolve("form.jpg");

        Files.write(pathPdf, bytesPdf);


        ProcessBuilder pb = new ProcessBuilder(
                "convert",
                "-density",
                "72",
                "-quality",
                "50",
                pathPdf.toString() + "[0]",
                pathPng.toString()
        );

        Process process = pb.start();
        process.waitFor();
        int response = process.waitFor();
        log.info("Response code imagemagick thumbnail geneation: " + response);

        byte[] result = IOUtils.toByteArray(new FileInputStream(pathPng.toFile()));

        // clean up
        FileUtils.deleteDirectory(dir.toFile());

        // return thumb
        return result;
    }


}
