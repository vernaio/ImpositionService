package de.perfectpattern.print.imposition.service.thumb;

import java.io.IOException;

public interface ThumbService {

    byte[] renderPdf(byte[] pdf) throws IOException, InterruptedException;
}
