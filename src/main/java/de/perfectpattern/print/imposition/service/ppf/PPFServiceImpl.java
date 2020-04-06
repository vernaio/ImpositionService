package de.perfectpattern.print.imposition.service.ppf;

import de.perfectpattern.print.imposition.model.CutBlock;
import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

@Controller
public class PPFServiceImpl implements PPFService {

    private static final Logger log = LoggerFactory.getLogger(PPFServiceImpl.class);

    @Override
    public byte[] createPPF(Sheet sheet) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bos);

        // ppf header
        writer.println("%!PS-Adobe-3.0");
        writer.println("%%CIP3-File Version 2.1");
        writer.println("CIP3BeginSheet");
        writer.println("/CIP3AdmJobName (" + sheet.getSheetId() + ") def");
        writer.println("/CIP3AdmSoftware (Chuck Norris) def");
        writer.println("/CIP3AdmPSExtent [" + String.format("%.2f %.2f", sheet.getSurfaceContentsBox().getWidth(), sheet.getSurfaceContentsBox().getHeight()) + "] def");
        writer.println("/CIP3AdmSheetLay /Right def");
        writer.println("CIP3BeginFront");

        // cut blocks
        writer.println("CIP3BeginCutData");

        for(CutBlock cutBlock: sheet.getCuttingParams()) {
            writeCutBlock(sheet.getSurfaceContentsBox(), cutBlock, writer);
        }

        writer.println("CIP3EndCutData");

        // ppf footer
        writer.println("CIP3EndFront");
        writer.println("CIP3EndSheet");
        writer.println("%%CIP3EndOfFile");

        // finalize
        writer.close();

        // return byte array
        return bos.toByteArray();
    }

    /**
     * Define a cut block for a position.
     * @param cutBlock The CutBlock object.
     * @param writer The writer
     */
    private void writeCutBlock(Rectangle contentBox, CutBlock cutBlock, PrintWriter writer) {
        Rectangle box = cutBlock.getBox();

        box = new Rectangle(
                contentBox.getWidth() - box.getUrx(),
                box.getLly(),
                contentBox.getWidth() - box.getLlx(),
                box.getUry()
        );

        // output
        writer.println("CIP3BeginCutBlock");
        writer.println("/CIP3BlockTrf [1.0 0.0 0.0 1.0 " + String.format("%.2f %.2f", box.getLlx(), box.getLly()) + "] def");
        writer.println("/CIP3BlockSize [" + String.format("%.2f %.2f", box.getWidth(), box.getHeight()) + "] def");
        writer.println("/CIP3BlockType /CutBlock def");
        writer.println("/CIP3BlockName (" + cutBlock.getBlockName() + ") def");
        writer.println("CIP3EndCutBlock");
    }
}
