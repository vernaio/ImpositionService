package de.perfectpattern.print.imposition.service.importer.specific;

import de.perfectpattern.commons.api.marshalling.XmlContext
import de.perfectpattern.commons.api.marshalling.XmlContextProvider
import de.perfectpattern.print.imposition.model.BinderySignature
import de.perfectpattern.print.imposition.model.CutBlock
import de.perfectpattern.print.imposition.model.Position
import de.perfectpattern.print.imposition.model.RunList
import de.perfectpattern.print.imposition.model.Sheet
import de.perfectpattern.print.imposition.model.SignatureCell
import de.perfectpattern.print.imposition.model.type.Border
import de.perfectpattern.print.imposition.model.type.FoldCatalog
import de.perfectpattern.print.imposition.model.type.Orientation
import de.perfectpattern.print.imposition.model.type.Priority
import de.perfectpattern.print.imposition.model.type.Rectangle
import de.perfectpattern.print.imposition.model.type.WorkStyle
import de.perfectpattern.print.imposition.model.type.XYPair
import de.perfectpattern.print.imposition.util.DimensionUtil
import de.perfectpattern.sPrint.one.v3.api.format.event.gangJob.DtoGangJobEvent
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.DtoGangJob
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.DtoWorkStyle
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.form.DtoFormBinderySignaturePlacement
import de.perfectpattern.sPrint.one.v3.api.format.workspace.DtoWorkspaces_ROOT

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils

// TODO
// change DtoWorkspaces_ROOT to gangjobevent

/**
 * The Importer for a Sprint One V3 file format.
 */
@Controller
class SprintOneV3Importer implements Importer {

    private static final Logger log = LoggerFactory.getLogger(SprintOneV3Importer.class)

    private final static String NS_SPO_V3 = 'http://www.perfectpattern.de/sPrint.one.v3.api'
		
		// Alex edit
		// private final DtoGangJobEvent hier hin, und das nutzen nachdem es accepted wurde (beim accepten auch initialisieren)
		// entweder final ohne static oder andersrum
		private DtoGangJobEvent dtoGJE;

    @Value('${SHEET_BLEED_MM}')
    private String sheetBleedMm;

//    @Value('${BOX_MARK_TO_FINAL_TRIM_THRESHOLD}')
//    private String boxMarkToFinalTrimThreshold;

    @Value('${BOX_MARK_TO_FINAL_TRIM_THRESHOLD:0}')
    private int boxMarkToFinalTrimThreshold;

    /**
     * Default constructor.
     */
    SprintOneV3Importer() {
    }

		// Alex edit:
		// Das hier ueberpruefen ob es ein DtoGangJobEvent ist
		// initalisierung erwartet byte[] -> bytearrayinputstream
    @Override
    boolean acceptDocument(byte[] bytes) {
        boolean result;
				
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				XmlContext xc = new XmlContextProvider().get();
				Object umo = xc.unmarshal(bais);
				if (umo instanceof DtoGangJobEvent) {
					dtoGJE = (DtoGangJobEvent) umo;
					result = true;
				} else {
					final String EMSG = "The provided XML file should be an instance of DtoGangJobEvent but is currently an instance of " + umo.getClass().getCanonicalName();
					log.error(EMSG)
					throw new IllegalArgumentException(EMSG);
				}
				
//        try {
//            result = new XmlSlurper().parse(new ByteArrayInputStream(bytes)).lookupNamespace('spoV3') == NS_SPO_V3
//
//        } catch (Exception ex) {
//            result = false
//        }

        return result
    }

    @Override
		//kann ohne byte bytes sein
    Sheet importDocument(byte[] bytes) {
        log.info("Sheet Bleed: " + sheetBleedMm + " mm")

        // parse
        def xml = new XmlSlurper().parse(new ByteArrayInputStream(bytes))

        // get gangjob node
        def gangJobs = xml.depthFirst().findAll { it.name() == 'gangJob' }

				// TODO
				// Howto?
				final int gjSize = gangJobs.size();
        if (gjSize > 1) {
            throw new IOException("Multiple GangJobs has been found... - Only one is supported.")
        }

        def gangJobXml = gangJobs[0]
				
        // extract positions (each placement is a position)
        def bsPlacements = gangJobXml.form.placementZone.binderySignaturePlacements.binderySignaturePlacement
				final List<DtoFormBinderySignaturePlacement> bsPlacements2 = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
        List<Position> positions = new ArrayList<>((int) bsPlacements.size())
        List<Position> positions2 = new ArrayList<>(bsPlacements2.size());
        List<CutBlock> cuttingParams = new ArrayList<>((int) bsPlacements.size());
        List<CutBlock> cuttingParams2 = new ArrayList<>(bsPlacements2.size());

        bsPlacements.eachWithIndex { it, idx ->
            positions.add(readPosition(it, gangJobXml))
            cuttingParams.add(readCutBlock(it, gangJobXml, idx))
        }
				
//				for (int i = 0; i < bsPlacements2size; i++) {
//					positions2.add(readPosition(bsPlacements2[i], gangJobXml))
//				}

        // create sheet id
        String partId = gangJobXml.'..'.@id.toString().substring(0, 4).toUpperCase()
				//partId = 
        int partLabel = Integer.parseInt(gangJobXml.'..'.@label.toString().toLowerCase().replace("job ", ""))
        String sheetId = String.format("%04d-%s", partLabel, partId)
				partId = dtoGJE.getId().substring(0, 4);
				final String partLabelS = dtoGJE.getLabel().split(" ")[1];
				final String sheetId2 = String.format("%s-%s", partLabelS, partId);

        // extract layoutTaskId
        String layoutTaskId;
        final String layoutTaskId2;
        String sourceRef = gangJobXml.'..'.@sourceRef.toString();
				final String sourceRef2 = dtoGJE.getSourceRef();

        if(!StringUtils.isEmpty(sourceRef)) {
            layoutTaskId = (sourceRef =~ /[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}/)[0];
            layoutTaskId2 = sourceRef2.split("layoutTasks/id=")[1].split("/result")[0];
        }

        // create sheet
        float mediaWidth = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@width.toFloat());
				final float mediaWidth2 = DimensionUtil.micro2dtp(dtoGJE.getGangJob().getMedia().getFormat().getWidth().floatValue());
        float mediaHeight = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@height.toFloat());
				final float mediaHeight2 = DimensionUtil.micro2dtp(dtoGJE.getGangJob().getMedia().getFormat().getHeight().floatValue());
        final Rectangle surfaceContentsBox = new Rectangle(0, 0, mediaWidth2, mediaHeight2);
				
				// TODO
				// don't use two enum classes?
				final DtoWorkStyle dtoWorkStyle = dtoGJE.getGangJob().getWorkStyle();
				WorkStyle workStyle;
				
				if (dtoWorkStyle.equals(DtoWorkStyle.SIMPLEX)) {
					workStyle = WorkStyle.Simplex;
				} else {
					workStyle = WorkStyle.WorkAndBack;
				}

        // sheet bleed
        float sheetBleedMm;

        try {
            sheetBleedMm = Float.parseFloat(this.sheetBleedMm);
        } catch (Exception ex) {
            log.error("Sheet Bleed is wrongly defined.", ex);
            throw ex;
        }

        sheetBleedMm = DimensionUtil.mm2dtp(sheetBleedMm);

				// TODO
				// this is not the same somehow?
        long latestEndTime = new Float(gangJobXml.'..'.@latestEndTime.toFloat()).longValue();
				final long latestEndTime2 = dtoGJE.getLatestEndTime();
				
				final int amount = dtoGJE.getGangJob().getQuantity();

        // create sheet
        Sheet sheet = new Sheet.Builder()
                .sheetId(sheetId)
                .bleed(sheetBleedMm)
                .layoutTaskId(layoutTaskId)
                .amount(amount)
                .latestEndTime(latestEndTime)
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
    private Position readPosition(def placement, def gangJobXml) {
			
			// TODO
			// muss aus Schleife kommen
			final List<DtoFormBinderySignaturePlacement> lp = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
			final DtoFormBinderySignaturePlacement lp0 = lp[0];
			
			// TODO
			// Warum wird hier 4 mal trim left genutzt?
      final Border clip =  new Border(Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()));
			final Border clip2 = new Border(Math.round((float)lp0.getTrim().left), Math.round((float)lp0.getTrim().left), Math.round((float)lp0.getTrim().left), Math.round((float)lp0.getTrim().left));
			
			
			// TODO
			// Warum float?
			
      // absolute box
      float llx = placement.offset.@x.toFloat() - placement.trim.@left.toFloat()
			final float llx2 = (float)(lp0.getOffset().getX() - lp0.getTrim().getLeft());
			
      float lly = placement.offset.@y.toFloat() - placement.trim.@bottom.toFloat()
			final float lly2 = (float)(lp0.getOffset().getY() - lp0.getTrim().getBottom());
			
      float urx = placement.offset.@x.toFloat() + placement.format.@width.toFloat() + placement.trim.@right.toFloat()
			final float urx2 = (float)(lp0.getOffset().getX() + lp0.getFormat().getWidth() + lp0.getTrim().getRight());
			
      float ury = placement.offset.@y.toFloat() + placement.format.@height.toFloat() + placement.trim.@top.toFloat()
			final float ury2 = (float)(lp0.getOffset().getY() + lp0.getFormat().getHeight() + lp0.getTrim().getTop());


      Rectangle absoluteBox = new Rectangle(
              DimensionUtil.micro2dtp(llx),
              DimensionUtil.micro2dtp(lly),
              DimensionUtil.micro2dtp(urx),
              DimensionUtil.micro2dtp(ury)
        )

			Rectangle absoluteBox2 = new Rectangle(
				DimensionUtil.micro2dtp(llx2),
				DimensionUtil.micro2dtp(lly2),
				DimensionUtil.micro2dtp(urx2),
				DimensionUtil.micro2dtp(ury2)
			  )
				
      // orientation
      Orientation orientation
      String rotation = placement.@rotation.toString()
			String rotation2 = lp0.getRotation();

      if (rotation == "ZERO") {
          orientation = Orientation.Rotate0
      } else if (rotation == "CC90") {
          orientation = Orientation.Rotate90
      } else if (rotation == "CC180") {
          orientation = Orientation.Rotate180
      } else if (rotation == "CC270") {
          orientation = Orientation.Rotate270
      } else {
					Log.error("Rotation '" + rotation + "' is not supported.");
        	throw new IOException("Rotation '" + rotation + "' is not supported.");
      }

      // load bindery signature
      String binderySignatureId = placement.binderySignatureRef.@id.toString()
			String binderySignatureId2 = lp0.getBinderySignatureRef().getId();
      BinderySignature binderySignature = readBinderySignature(binderySignatureId, placement, gangJobXml, orientation, placement.@flipped.toBoolean())
    	BinderySignature binderySignature2 = readBinderySignature(binderySignatureId2, placement, gangJobXml, orientation, placement.@flipped.toBoolean())

      boolean allowsBoxMark =
              ((binderySignature.getInnerContentFrame().getBottom(orientation)+clip.getBottom())>=this.boxMarkToFinalTrimThreshold)&&
              ((binderySignature.getInnerContentFrame().getTop(orientation)+clip.getTop())>=this.boxMarkToFinalTrimThreshold)&&
              ((binderySignature.getInnerContentFrame().getLeft(orientation)+clip.getLeft())>=this.boxMarkToFinalTrimThreshold)&&
              ((binderySignature.getInnerContentFrame().getRight(orientation)+clip.getRight())>=this.boxMarkToFinalTrimThreshold);

      // create and return position object
      return new Position.Builder()
              .orientation(orientation)
              .binderySignature(binderySignature)
              .absoluteBox(absoluteBox)
              .allowsBoxMark(allowsBoxMark)
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

          final Border innerContentFrame;

          if (FoldCatalog.F2_1 == foldCatalog) {
              signatureCells = new ArrayList<>(1)
              signatureCells.add(createSignatureCellF2(bsXml, positionXml, orientation))

              innerContentFrame=new Border(0L);

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

              innerContentFrame=new Border(
                      Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@headTrim.toFloat()).longValue(),
                      Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@footTrim.toFloat()).longValue(),
                      Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@faceTrim.toFloat()).longValue(),
                      Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "1" }.@faceTrim.toFloat()).longValue());

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
                  .innerContentFrame(innerContentFrame)
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
                positionXml.selectedPrintData.frontPage.@pdfUrl.toString(),
                positionXml.selectedPrintData.frontPage.@pdfPageNumber.toInteger() - 1
        )

        RunList pageBack

        if(StringUtils.isEmpty(positionXml.selectedPrintData.backPage.@pdfUrl.toString())) {
            pageBack = null
        } else {
            pageBack = new RunList(
                    positionXml.selectedPrintData.backPage.@pdfUrl.toString(),
                    positionXml.selectedPrintData.backPage.@pdfPageNumber.toInteger() - 1
            )
        }
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
                strippingCell.frontPage.printData.@pdfUrl.toString(),
                strippingCell.frontPage.printData.@pdfPageNumber.toInteger() - 1
        )
        RunList pageBack = new RunList(
                strippingCell.backPage.printData.@pdfUrl.toString(),
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
