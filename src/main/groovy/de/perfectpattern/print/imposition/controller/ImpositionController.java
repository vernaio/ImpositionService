package de.perfectpattern.print.imposition.controller;

import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.service.identification.IdentificationService;
import de.perfectpattern.print.imposition.service.importer.ImporterService;
import de.perfectpattern.print.imposition.service.imposition.ImpositionService;
import de.perfectpattern.print.imposition.service.ppf.PPFService;
import de.perfectpattern.print.imposition.service.thumb.ThumbService;
import de.perfectpattern.print.imposition.service.xjdf.XJDFService;
import de.perfectpattern.print.imposition.util.DimensionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/impose")
public class ImpositionController {

    private static final Logger log = LoggerFactory.getLogger(ImpositionController.class);

    @Autowired
    private ImporterService importerService;

    @Autowired
    private ImpositionService impositionService;

    @Autowired
    private ThumbService thumbService;

    @Autowired
    private IdentificationService identificationService;

    @Autowired
    private XJDFService xjdfService;

    @Autowired
    private PPFService ppfService;

    @RequestMapping(value = "/sheet", method = RequestMethod.POST, produces = "application/zip")
    public byte[] imposeSheet(@RequestBody byte[] bytes) throws Exception {
        log.info("New sheet has been received... (" + DimensionUtil.bytes2readable(bytes.length) + ")");

        try {

            // import document
            Sheet sheet = importerService.importDocument(bytes);

            if (sheet == null) {
                log.warn("No importer has accepted the file.");
                throw new IOException("No importer has accepted the file.");
            }

            log.info("Sheet " + sheet.getSheetId() + " has been imported.");

            // impose sheet
            byte[] artwork = impositionService.impose(sheet);
            log.info("Sheet " + sheet.getSheetId() + " has been imposed.");

            // create thumb
            byte[] thumb = thumbService.renderPdf(artwork);
            log.info("Thumb sheet " + sheet.getSheetId() + " has been generated.");

            // create identification
            byte[] identification = identificationService.generate(sheet, thumb);
            log.info("Identification PDF sheet " + sheet.getSheetId() + " has been generated.");

            // build xjdf
            byte[] xjdf = xjdfService.createXJDF(sheet, artwork, thumb, identification);
            log.info("XJDF Package sheet " + sheet.getSheetId() + "has been generated.");

            // return xjdf
            log.info("Send XJDF response (" + DimensionUtil.bytes2readable(xjdf.length) + ")...");
            return xjdf;


        } catch (Exception ex) {
            log.error("Error processing sheet.", ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/sheet/pdf", method = RequestMethod.POST, produces = "application/pdf")
    public byte[] imposeSheetPdf(@RequestBody byte[] bytes) throws Exception {
        log.info("New Sheet Artwork-PDF creation... (" + DimensionUtil.bytes2readable(bytes.length) + ")");

        try {

            // import document
            Sheet sheet = importerService.importDocument(bytes);

            if (sheet == null) {
                log.warn("No importer has accepted the file.");
                throw new IOException("No importer has accepted the file.");
            }

            log.info("Sheet " + sheet.getSheetId() + " has been imported.");

            // impose sheet
            byte[] artwork = impositionService.impose(sheet);
            log.info("Sheet " + sheet.getSheetId() + " has been imposed.");

            // return xjdf
            log.info("Send PDF response (" + DimensionUtil.bytes2readable(artwork.length) + ")...");
            return artwork;


        } catch (Exception ex) {
            log.error("Error processing sheet.", ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/sheet/identification", method = RequestMethod.POST, produces = "application/pdf")
    public byte[] imposeSheetIdentification(@RequestBody byte[] bytes) throws Exception {
        log.info("New Sheet Identification-PDF creation... (" + DimensionUtil.bytes2readable(bytes.length) + ")");

        try {

            // import document
            Sheet sheet = importerService.importDocument(bytes);

            if (sheet == null) {
                log.warn("No importer has accepted the file.");
                throw new IOException("No importer has accepted the file.");
            }

            log.info("Sheet " + sheet.getSheetId() + " has been imported.");

            // impose sheet
            byte[] artwork = impositionService.impose(sheet);
            log.info("Sheet " + sheet.getSheetId() + " has been imposed.");

            // create thumb
            byte[] thumb = thumbService.renderPdf(artwork);
            log.info("Thumb sheet " + sheet.getSheetId() + " has been generated.");

            // create identification
            byte[] identification = identificationService.generate(sheet, thumb);
            log.info("Identification PDF sheet " + sheet.getSheetId() + " has been generated.");

            // return identification
            log.info("Send Identification response (" + DimensionUtil.bytes2readable(identification.length) + ")...");
            return identification;


        } catch (Exception ex) {
            log.error("Error processing sheet.", ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/sheet/ppf", method = RequestMethod.POST, produces = "text/plain")
    public byte[] imposeSheetPpf(@RequestBody byte[] bytes) throws Exception {
        log.info("New Sheet PPF creation... (" + DimensionUtil.bytes2readable(bytes.length) + ")");

        try {

            // import document
            Sheet sheet = importerService.importDocument(bytes);

            if (sheet == null) {
                log.warn("No importer has accepted the file.");
                throw new IOException("No importer has accepted the file.");
            }

            log.info("Sheet " + sheet.getSheetId() + " has been imported.");

            // create ppf
            byte[] ppf = ppfService.createPPF(sheet);

            log.info("Send PPF response (" + DimensionUtil.bytes2readable(ppf.length) + ")...");
            return ppf;

        } catch (Exception ex) {
            log.error("Error processing sheet.", ex);
            throw ex;
        }
    }

}
