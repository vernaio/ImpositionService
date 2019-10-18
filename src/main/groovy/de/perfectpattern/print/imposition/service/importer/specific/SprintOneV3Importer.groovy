package de.perfectpattern.print.imposition.service.importer.specific

import de.perfectpattern.print.imposition.model.BinderySignature
import de.perfectpattern.print.imposition.model.CutBlock
import de.perfectpattern.print.imposition.model.Position
import de.perfectpattern.print.imposition.model.RunList
import de.perfectpattern.print.imposition.model.Sheet
import de.perfectpattern.print.imposition.model.SignatureCell
import de.perfectpattern.print.imposition.model.type.FoldCatalog
import de.perfectpattern.print.imposition.model.type.Orientation
import de.perfectpattern.print.imposition.model.type.Priority
import de.perfectpattern.print.imposition.model.type.Rectangle
import de.perfectpattern.print.imposition.model.type.WorkStyle
import de.perfectpattern.print.imposition.model.type.XYPair
import de.perfectpattern.print.imposition.util.DimensionUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils

import java.nio.file.Paths

/**
 * The Importer for a Sprint One V3 file format.
 */
@Controller
class SprintOneV3Importer implements Importer {

    private static final Logger log = LoggerFactory.getLogger(SprintOneV3Importer.class)

    private final static String NS_SPO_V3 = 'http://www.perfectpattern.de/sPrint.one.v3.api'

    @Value('${SHEET_BLEED_MM}')
    private String sheetBleedMm;

    /**
     * Default constructor.
     */
    SprintOneV3Importer() {
    }

    @Override
    boolean acceptDocument(byte[] bytes) {
        boolean result

        try {
            result = new XmlSlurper().parse(new ByteArrayInputStream(bytes)).lookupNamespace('spoV3') == NS_SPO_V3

        } catch (Exception ex) {
            result = false
        }

        return result
    }

    @Override
    Sheet importDocument(byte[] bytes) {
        log.info("Sheet Bleed: " + sheetBleedMm + " mm")

        // parse
        def xml = new XmlSlurper().parse(new ByteArrayInputStream(bytes))

        // get gangjob node
        def gangJobs = xml.depthFirst().findAll { it.name() == 'gangJob' }

        if (gangJobs.size() > 1) {
            throw new IOException("Multiple GangJobs has been found... - Only one is supported.")
        }

        def gangJobXml = gangJobs[0]

        // extract positions (each placement is a position)
        def bsPlacements = gangJobXml.form.placementZone.binderySignaturePlacements.binderySignaturePlacement
        List<Position> positions = new ArrayList<>((int) bsPlacements.size())
        List<CutBlock> cuttingParams = new ArrayList<>((int) bsPlacements.size());

        bsPlacements.eachWithIndex { it, idx ->
            positions.add(readPosition(it, gangJobXml))
            cuttingParams.add(readCutBlock(it, gangJobXml, idx))
        }

        // create sheet id
        String partId = gangJobXml.'..'.@id.toString().substring(0, 4).toUpperCase();
        int partLabel = Integer.parseInt(gangJobXml.'..'.@label.toString().toLowerCase().replace("job ", ""));
        String sheetId = String.format("%04d-%s", partLabel, partId)

        // extract layoutTaskId
        String layoutTaskId = null
        String sourceRef = gangJobXml.'..'.@sourceRef.toString()

        if(!StringUtils.isEmpty(sourceRef)) {
            layoutTaskId = (sourceRef =~ /[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}/)[0]
        }

        // create sheet
        float mediaWidth = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@width.toFloat())
        float mediaHeight = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@height.toFloat())
        Rectangle surfaceContentsBox = new Rectangle(0, 0, mediaWidth, mediaHeight)

        WorkStyle workStyle

        if (gangJobXml.@workStyle.toString() == "SIMPLEX") {
            workStyle = WorkStyle.Simplex
        } else {
            workStyle = WorkStyle.WorkAndBack
        }

        // sheet bleed
        float sheetBleedMm

        try {
            sheetBleedMm = Float.parseFloat(this.sheetBleedMm);
        } catch (Exception ex) {
            log.error("Sheet Bleed is wrongly defined.", ex)
            throw ex;
        }

        sheetBleedMm = DimensionUtil.mm2dtp(sheetBleedMm);

        // create sheet
        Sheet sheet = new Sheet.Builder()
                .sheetId(sheetId)
                .bleed(sheetBleedMm)
                .layoutTaskId(layoutTaskId)
                .amount((int) gangJobXml.@quantity.toInteger())
                .workStyle(workStyle)
                .surfaceContentsBox(surfaceContentsBox)
                .cuttingParams(cuttingParams)
                .positions(positions)
                .build()


        // return sheet
        return sheet
    }

    /**
     * Imports a single cut block. (BinderySignaturePlacement)
     * @param placement The Placement to be imported.
     * @param gangJobXml The GangJob XML.
     * @return The Position object.
     */
    private static CutBlock readCutBlock(def placement, def gangJobXml, def idx) {

        // box
        float llx = placement.offset.@x.toFloat()
        float lly = placement.offset.@y.toFloat()
        float urx = placement.offset.@x.toFloat() + placement.format.@width.toFloat()
        float ury = placement.offset.@y.toFloat() + placement.format.@height.toFloat()

        Rectangle box = new Rectangle(
                DimensionUtil.micro2dtp(llx),
                DimensionUtil.micro2dtp(lly),
                DimensionUtil.micro2dtp(urx),
                DimensionUtil.micro2dtp(ury)
        )

        // block name
        String blockName = "BLOCK-" + idx

        // create and return cut block object
        return new CutBlock.Builder()
                .blockName(blockName)
                .box(box)
                .build()
    }


    /**
     * Imports a single position. (BinderySignaturePlacement)
     * @param placement The position XML to be imported.
     * @param gangJobXml The GangJob XML.
     * @return The Position object.
     */
    private static Position readPosition(def placement, def gangJobXml) {

        // absolute box
        float llx = placement.offset.@x.toFloat() - placement.trim.@left.toFloat()
        float lly = placement.offset.@y.toFloat() - placement.trim.@bottom.toFloat()
        float urx = placement.offset.@x.toFloat() + placement.format.@width.toFloat() + placement.trim.@right.toFloat()
        float ury = placement.offset.@y.toFloat() + placement.format.@height.toFloat() + placement.trim.@top.toFloat()

        Rectangle absoluteBox = new Rectangle(
                DimensionUtil.micro2dtp(llx),
                DimensionUtil.micro2dtp(lly),
                DimensionUtil.micro2dtp(urx),
                DimensionUtil.micro2dtp(ury)
        )

        // orientation
        Orientation orientation
        String rotation = placement.@rotation.toString()

        if (rotation == "ZERO") {
            orientation = Orientation.Rotate0
        } else if (rotation == "CC90") {
            orientation = Orientation.Rotate90
        } else if (rotation == "CC180") {
            orientation = Orientation.Rotate180
        } else if (rotation == "CC270") {
            orientation = Orientation.Rotate270
        } else {
            throw new IOException("Rotation '" + rotation + "' is not supported.")
        }

        // load bindery signature
        String binderySignatureId = placement.binderySignatureRef.@id.toString()
        BinderySignature binderySignature = readBinderySignature(binderySignatureId, placement, gangJobXml, orientation, placement.@flipped.toBoolean())

        // create and return position object
        return new Position.Builder()
                .orientation(orientation)
                .binderySignature(binderySignature)
                .absoluteBox(absoluteBox)
                .build()
    }

    /**
     * Imports a single bindery signature.
     * @param id The identifier of the bindery signature.
     * @param gangJobXml The gangJob xml
     * @return The bindery signature as object.
     */
    private static BinderySignature readBinderySignature(String id, def positionXml, def gangJobXml, Orientation orientation, boolean flipped) {
        BinderySignature binderySignature = null;

        // find bindery signature node
        def bsXml = gangJobXml.binderySignatures.binderySignature.find { it.@id == id }

        if (bsXml.size() == 1) {

            // bindery signature size
            float heightDtp = DimensionUtil.micro2dtp(
                    (float) (bsXml.trimFormat.@height.toFloat() + getTrimHead(positionXml, orientation) + getTrimFoot(positionXml, orientation))
            )
            float widthDtp = DimensionUtil.micro2dtp(
                    (float) (bsXml.trimFormat.@width.toFloat() + getTrimFace(positionXml, orientation) + getTrimSpine(positionXml, orientation))
            )

            XYPair binderySignatureSize = new XYPair(widthDtp, heightDtp)

            // fold catalog
            String strFoldCatalog = bsXml.signature.signatureTypeRef.@id.toString();

            FoldCatalog foldCatalog

            if (StringUtils.isEmpty(strFoldCatalog)) {
                foldCatalog = FoldCatalog.F2_1
            } else {
                foldCatalog = FoldCatalog.findByName(strFoldCatalog)
            }

            // bindery signature context
            Integer bsNumberTotal = null, bsNumberCurrent = null

            if(bsXml.signature.relatedSignatureRefs.size() > 0) {
                bsNumberTotal = bsXml.signature.relatedSignatureRefs.ref.size()

                // validate index path for collecting only
                boolean isValid = true

                bsXml.signature.relatedSignatureRefs.ref.each {
                    if(!it.@indexPath.toString().matches("0( 0)*")) {
                        isValid = false
                        log.warn("IndexPath '" + it.@indexPath + "' is not supported.")
                    }
                }

                if(isValid) {
                    String signatureId = bsXml.signature.@id
                    String[] indexPath = bsXml.signature.relatedSignatureRefs.ref.find {
                        it.@binderySignatureRef == signatureId
                    }.@indexPath.toString().split(" ")

                    bsNumberCurrent = indexPath.length
                }
            }

            // priority
            Priority priority = Priority.findByValue(bsXml.@priority.toInteger());

            // extract signature cells
            List<SignatureCell> signatureCells;

            if (FoldCatalog.F2_1 == foldCatalog) {
                signatureCells = new ArrayList<>(1)
                signatureCells.add(createSignatureCellF2(bsXml, positionXml, orientation))

            } else if (FoldCatalog.F4_1 == foldCatalog) {
                signatureCells = new ArrayList<>(bsXml.signature.strippingCells.strippingCell.size())

                // row index 0
                def col_0 = bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }

                signatureCells.add(
                        createSignatureCellF4(col_0)
                )

                // row index 1
                def col_1 = bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "1" }

                signatureCells.add(
                        createSignatureCellF4(col_1)
                )


            } else {

                log.error("Fold Schema is not supported.")
                throw new Exception("FoldSchema is not supported.")
            }

            // create bindery signature object
            binderySignature = new BinderySignature.Builder()
                    .label(bsXml.@label.toString())
                    .amount((int) bsXml.@mustDemand.toInteger())
                    .priority(priority)
                    .jobId(bsXml.@orderRef.toString())
                    .foldCatalog(foldCatalog)
                    .binderySignatureSize(binderySignatureSize)
                    .signatureCells(signatureCells)
                    .flipped(flipped)
                    .bsNumberTotal(bsNumberTotal)
                    .bsNumberCurrent(bsNumberCurrent)
                    .build()
        }

        // return bindery signature object
        return binderySignature
    }

    /**
     * Import the signature cells for a F2-1 folding scheme bindery signature.
     * @param positionXml The Position (BindeySignaturesPlacement) XML snippet.
     * @return The extracted information of the SignatureCell
     */
    private static SignatureCell createSignatureCellF2(def bsXml, def positionXml, Orientation orientation) {

        // bleed
        float bleedFace = DimensionUtil.micro2dtp(bsXml.bleed.@right.toFloat())
        float bleedFoot = DimensionUtil.micro2dtp(bsXml.bleed.@bottom.toFloat())
        float bleedHead = DimensionUtil.micro2dtp(bsXml.bleed.@top.toFloat())
        float bleedSpine = DimensionUtil.micro2dtp(bsXml.bleed.@left.toFloat())

        // trim
        float trimFace = DimensionUtil.micro2dtp(getTrimFace(positionXml, orientation))
        float trimFoot = DimensionUtil.micro2dtp(getTrimFoot(positionXml, orientation))
        float trimHead = DimensionUtil.micro2dtp(getTrimHead(positionXml, orientation))
        float trimSpine = DimensionUtil.micro2dtp(getTrimSpine(positionXml, orientation))

        XYPair trimSize = new XYPair(
                DimensionUtil.micro2dtp(bsXml.trimFormat.@width.toFloat()),
                DimensionUtil.micro2dtp(bsXml.trimFormat.@height.toFloat())
        )

        // pages
        RunList pageFront = new RunList(
                Paths.get(positionXml.selectedPrintData.frontPage.@pdfUrl.toString()),
                positionXml.selectedPrintData.frontPage.@pdfPageNumber.toInteger() - 1
        )
        RunList pageBack = new RunList(
                Paths.get(positionXml.selectedPrintData.backPage.@pdfUrl.toString()),
                positionXml.selectedPrintData.backPage.@pdfPageNumber.toInteger() - 1
        )
        int pageIndexFront = 0
        int pageIndexBack = 1

        // create and return object
        return new SignatureCell.Builder()
                .bleedFace(bleedFace)
                .bleedFoot(bleedFoot)
                .bleedHead(bleedHead)
                .bleedSpine(bleedSpine)
                .trimFace(trimFace)
                .trimFoot(trimFoot)
                .trimHead(trimHead)
                .trimSpine(trimSpine)
                .trimSize(trimSize)
                .pageIndexFront(pageIndexFront)
                .pageIndexBack(pageIndexBack)
                .pageFront(pageFront)
                .pageBack(pageBack)
                .orientation(Orientation.Rotate0)
                .build()
    }

    /**
     * Import the signature cells for a F4-1 folding scheme bindery signature.
     * @param strippingCell The BindeySignatures SignatureCell XML snippet.
     * @return The extracted information of the SignatureCell
     */
    private static SignatureCell createSignatureCellF4(def strippingCell) {

        // bleed
        float bleedFace = DimensionUtil.micro2dtp(strippingCell.@faceBleed.toFloat())
        float bleedFoot = DimensionUtil.micro2dtp(strippingCell.@footBleed.toFloat())
        float bleedHead = DimensionUtil.micro2dtp(strippingCell.@headBleed.toFloat())
        float bleedSpine = DimensionUtil.micro2dtp(strippingCell.@spineBleed.toFloat())

        // trim
        float trimFace = DimensionUtil.micro2dtp(strippingCell.@faceTrim.toFloat())
        float trimFoot = DimensionUtil.micro2dtp(strippingCell.@footTrim.toFloat())
        float trimHead = DimensionUtil.micro2dtp(strippingCell.@headTrim.toFloat())
        float trimSpine = DimensionUtil.micro2dtp(strippingCell.@spineTrim.toFloat())

        XYPair trimSize = new XYPair(
                DimensionUtil.micro2dtp(strippingCell.intrinsicTrimBoxFormat.@width.toFloat()),
                DimensionUtil.micro2dtp(strippingCell.intrinsicTrimBoxFormat.@height.toFloat())
        )

        // creep compensation
        float creepPercent = strippingCell.@creepCompensation.toFloat()
        float creepAbsolute = trimSize.getX() * (1 - creepPercent)

        trimSize = new XYPair(
                (float) (trimSize.getX() - creepAbsolute),
                trimSize.getY()
        )

        trimFace += creepAbsolute;

        // pages
        RunList pageFront = new RunList(
                Paths.get(strippingCell.frontPage.printData.@pdfUrl.toString()),
                strippingCell.frontPage.printData.@pdfPageNumber.toInteger() - 1
        )
        RunList pageBack = new RunList(
                Paths.get(strippingCell.backPage.printData.@pdfUrl.toString()),
                strippingCell.backPage.printData.@pdfPageNumber.toInteger() - 1
        )
        int pageIndexFront = strippingCell.frontPage.@index.toInteger()
        int pageIndexBack = strippingCell.backPage.@index.toInteger()

        // orientation
        Orientation orientation

        if ("DOWN".equals(strippingCell.@orientation.toString())) {
            orientation = Orientation.Rotate180

        } else if ("LEFT".equals(strippingCell.@orientation.toString())) {
            orientation = Orientation.Rotate90

        } else if ("RIGHT".equals(strippingCell.@orientation.toString())) {
            orientation = Orientation.Rotate270

        } else {
            orientation = Orientation.Rotate0
        }

        // create and return object
        return new SignatureCell.Builder()
                .bleedFace(bleedFace)
                .bleedFoot(bleedFoot)
                .bleedHead(bleedHead)
                .bleedSpine(bleedSpine)
                .trimFace(trimFace)
                .trimFoot(trimFoot)
                .trimHead(trimHead)
                .trimSpine(trimSpine)
                .trimSize(trimSize)
                .pageIndexFront(pageIndexFront)
                .pageIndexBack(pageIndexBack)
                .pageFront(pageFront)
                .pageBack(pageBack)
                .orientation(orientation)
                .build()
    }

    private static float getTrimFace(def positionXml, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
            trimValue = positionXml.trim.@right.toFloat()

        } else if (Orientation.Rotate90 == orientation) {
            trimValue = positionXml.trim.@bottom.toFloat()

        } else if (Orientation.Rotate180 == orientation) {
            trimValue = positionXml.trim.@left.toFloat()

        } else if (Orientation.Rotate270 == orientation) {
            trimValue = positionXml.trim.@top.toFloat()

        }

        return trimValue;
    }

    private static float getTrimHead(def positionXml, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
            trimValue = positionXml.trim.@top.toFloat()

        } else if (Orientation.Rotate90 == orientation) {
            trimValue = positionXml.trim.@right.toFloat()

        } else if (Orientation.Rotate180 == orientation) {
            trimValue = positionXml.trim.@bottom.toFloat()

        } else if (Orientation.Rotate270 == orientation) {
            trimValue = positionXml.trim.@left.toFloat()

        }

        return trimValue;
    }

    private static float getTrimSpine(def positionXml, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
            trimValue = positionXml.trim.@left.toFloat()

        } else if (Orientation.Rotate90 == orientation) {
            trimValue = positionXml.trim.@top.toFloat()

        } else if (Orientation.Rotate180 == orientation) {
            trimValue = positionXml.trim.@right.toFloat()

        } else if (Orientation.Rotate270 == orientation) {
            trimValue = positionXml.trim.@bottom.toFloat()

        }

        return trimValue;
    }

    private static float getTrimFoot(def positionXml, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
            trimValue = positionXml.trim.@bottom.toFloat()

        } else if (Orientation.Rotate90 == orientation) {
            trimValue = positionXml.trim.@left.toFloat()

        } else if (Orientation.Rotate180 == orientation) {
            trimValue = positionXml.trim.@top.toFloat()

        } else if (Orientation.Rotate270 == orientation) {
            trimValue = positionXml.trim.@right.toFloat()

        }

        return trimValue;
    }
}
