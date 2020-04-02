package de.perfectpattern.print.imposition.service.importer.specific;

import de.perfectpattern.commons.api.marshalling.XmlContext;
import de.perfectpattern.commons.api.marshalling.XmlContextProvider;
import de.perfectpattern.print.imposition.model.BinderySignature;
import de.perfectpattern.print.imposition.model.CutBlock;
import de.perfectpattern.print.imposition.model.Position;
import de.perfectpattern.print.imposition.model.RunList;
import de.perfectpattern.print.imposition.model.Sheet;
import de.perfectpattern.print.imposition.model.SignatureCell;
import de.perfectpattern.print.imposition.model.type.Border;
import de.perfectpattern.print.imposition.model.type.FoldCatalog;
import de.perfectpattern.print.imposition.model.type.Orientation;
import de.perfectpattern.print.imposition.model.type.Priority;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.model.type.WorkStyle;
import de.perfectpattern.print.imposition.model.type.XYPair;
import de.perfectpattern.print.imposition.util.DimensionUtil;
import de.perfectpattern.sPrint.one.v3.api.format.assembler.parameters.signatureType.DtoOrientation;
import de.perfectpattern.sPrint.one.v3.api.format.assembler.result.DtoSignatureRef;
import de.perfectpattern.sPrint.one.v3.api.format.assembler.result.DtoStrippingCell;
import de.perfectpattern.sPrint.one.v3.api.format.binderySignature.DtoBinderySignature;
import de.perfectpattern.sPrint.one.v3.api.format.event.gangJob.DtoGangJobEvent;
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.DtoGangJob;
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.DtoWorkStyle;
import de.perfectpattern.sPrint.one.v3.api.format.gangJob.form.DtoFormBinderySignaturePlacement;
import de.perfectpattern.sPrint.one.v3.api.format.util.DtoBorder;
import de.perfectpattern.sPrint.one.v3.api.format.util.DtoFormat;
import de.perfectpattern.sPrint.one.v3.api.format.util.DtoRotation;
import de.perfectpattern.sPrint.one.v3.api.format.workspace.DtoWorkspaces_ROOT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * The Importer for a Sprint One V3 file format.
 */
@Controller
class SprintOneV3Importer implements Importer {

    private static final Logger log = LoggerFactory.getLogger(SprintOneV3Importer.class);

    private final static String NS_SPO_V3 = "http://www.perfectpattern.de/sPrint.one.v3.api";
		
		// Alex edit
		// private final DtoGangJobEvent hier hin, und das nutzen nachdem es accepted wurde (beim accepten auch initialisieren)
		// entweder final ohne static oder andersrum
		private DtoGangJobEvent dtoGJE;

    @Value("${SHEET_BLEED_MM}")
    private String sheetBleedMm;

//    @Value('${BOX_MARK_TO_FINAL_TRIM_THRESHOLD}')
//    private String boxMarkToFinalTrimThreshold;

    @Value("${BOX_MARK_TO_FINAL_TRIM_THRESHOLD:0}")
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
		public boolean acceptDocument(byte[] bytes) {
        boolean result;
				
				if (bytes.length < 2) {
					return false;
				}
				
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				XmlContext xc = new XmlContextProvider().get();
				final Object umo;
				try { 
					umo = xc.unmarshal(bais);
				} catch (RuntimeException e) {
					log.error("Input could not be unmarshaled");
//					throw new RuntimeException("Input could not be unmarshaled");
					return false;
				}
				if (umo instanceof DtoGangJobEvent) {
					dtoGJE = (DtoGangJobEvent) umo;
					result = true;
				} else {
					final String EMSG = "The provided XML file should be an instance of DtoGangJobEvent but is currently an instance of " + umo.getClass().getCanonicalName();
					log.error(EMSG);
					throw new IllegalArgumentException(EMSG);
				}
				
//        try {
//            result = new XmlSlurper().parse(new ByteArrayInputStream(bytes)).lookupNamespace('spoV3') == NS_SPO_V3
//
//        } catch (Exception ex) {
//            result = false
//        }

        return result;
    }

		// TODO
		// kann ohne byte bytes sein
    @Override
    public Sheet importDocument(byte[] bytes) throws Exception {
        log.info("Sheet Bleed: " + sheetBleedMm + " mm");
				
				// Newly added:
				if (!acceptDocument(bytes)) {
					log.error("Document was not accepted!");
					throw new RuntimeException("Document was not accepted!");
				}

//        // parse
//        def xml = new XmlSlurper().parse(new ByteArrayInputStream(bytes))
//
//        // get gangjob node
//        def gangJobs = xml.depthFirst().findAll { it.name() == 'gangJob' }

//				// TODO
//				// Howto?
//				final int gjSize = gangJobs.size();
////				final int gjSize3 = dtoGJE.getGangJob().getBinderySig
//        if (gjSize > 1) {
//            throw new IOException("Multiple GangJobs has been found... - Only one is supported.")
//        }

//        def gangJobXml = gangJobs[0]
				
        // extract positions (each placement is a position)
//        def bsPlacements = gangJobXml.form.placementZone.binderySignaturePlacements.binderySignaturePlacement
				final List<DtoFormBinderySignaturePlacement> bsPlacements = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
//        List<Position> positions = new ArrayList<>((int) bsPlacements.size())
        List<Position> positions = new ArrayList<>(bsPlacements.size());
//        List<CutBlock> cuttingParams = new ArrayList<>((int) bsPlacements.size());
        List<CutBlock> cuttingParams = new ArrayList<>(bsPlacements.size());

//        bsPlacements.eachWithIndex { it, idx ->
////            positions.add(readPosition(it, gangJobXml))
////            cuttingParams.add(readCutBlock(it, gangJobXml, idx))
//            cuttingParams.add(readCutBlock(it, idx))
//        }
//				
				for (int i = 0; i < bsPlacements.size(); i++) {
					positions.add(readPosition(bsPlacements.get(i)));
					cuttingParams.add(readCutBlock(bsPlacements.get(i), i));
				}

        // create sheet id
//        String partId = gangJobXml.'..'.@id.toString().substring(0, 4).toUpperCase()
				//partId = 
//        int partLabel = Integer.parseInt(gangJobXml.'..'.@label.toString().toLowerCase().replace("job ", ""))
//        String sheetId = String.format("%04d-%s", partLabel, partId)
				final String partId = dtoGJE.getId().substring(0, 4).toUpperCase();
				
				final int partLabel;
				if (dtoGJE.getLabel().contains(" ")) {
					partLabel = Integer.parseInt(dtoGJE.getLabel().split(" ")[1]);
				} else {
					partLabel = Integer.parseInt(dtoGJE.getLabel());
				}
				
				
				final String sheetId = String.format("%04d-%s", partLabel, partId);

        // extract layoutTaskId
//        String layoutTaskId;
        final String layoutTaskId;
//        String sourceRef = gangJobXml.'..'.@sourceRef.toString();
				final String sourceRef = dtoGJE.getSourceRef();

        if(!StringUtils.isEmpty(sourceRef)) {
//            layoutTaskId = (sourceRef =~ /[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{4}\-[0-9a-fA-F]{12}/)[0];
          layoutTaskId = sourceRef.split("layoutTasks/id=")[1].split("/result")[0];
        } else {
        	layoutTaskId = "";
        }

        // create sheet
//        float mediaWidth = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@width.toFloat());
				final float mediaWidth = DimensionUtil.micro2dtp((float) dtoGJE.getGangJob().getMedia().getFormat().getWidth());
//        float mediaHeight = DimensionUtil.micro2dtp((float) gangJobXml.media.format.@height.toFloat());
				final float mediaHeight = DimensionUtil.micro2dtp((float) dtoGJE.getGangJob().getMedia().getFormat().getHeight());
        final Rectangle surfaceContentsBox = new Rectangle(0, 0, mediaWidth, mediaHeight);
				
				// TODO
				// don't use two enum classes?
				final DtoWorkStyle dtoWorkStyle = dtoGJE.getGangJob().getWorkStyle();
				final WorkStyle workStyle;
				
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
				// groovy line gives an incorrect value
//        long latestEndTime = new Float(gangJobXml.'..'.@latestEndTime.toFloat()).longValue();
				final long latestEndTime = dtoGJE.getLatestEndTime();
				
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
                .build();


        // return sheet
        return sheet;
    }

    /**
     * Imports a single cut block. (BinderySignaturePlacement)
     * @param placement The Placement to be imported.
     * @param gangJobXml The GangJob XML.
     * @return The Position object.
     */
    private CutBlock readCutBlock(DtoFormBinderySignaturePlacement dtoFBSP, int i) {
			
			// TODO
			// muss aus Schleife kommen
//			final List<DtoFormBinderySignaturePlacement> lp = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
//			final DtoFormBinderySignaturePlacement lp0 = lp[0];

      // box
//      float llx = placement.offset.@x.toFloat()
//      float lly = placement.offset.@y.toFloat()
//      float urx = placement.offset.@x.toFloat() + placement.format.@width.toFloat()
//      float ury = placement.offset.@y.toFloat() + placement.format.@height.toFloat()
			
			final float llx = (float)(dtoFBSP.getOffset().getX());
			final float lly = (float)(dtoFBSP.getOffset().getY());
			final float urx = (float)(dtoFBSP.getOffset().getX() + dtoFBSP.getFormat().getWidth());
			final float ury = (float)(dtoFBSP.getOffset().getY() + dtoFBSP.getFormat().getHeight());


      final Rectangle box = new Rectangle(
              DimensionUtil.micro2dtp(llx),
              DimensionUtil.micro2dtp(lly),
              DimensionUtil.micro2dtp(urx),
              DimensionUtil.micro2dtp(ury)
      );

      // block name
      final String blockName = "BLOCK-" + i;

      // create and return cut block object
      return new CutBlock.Builder()
              .blockName(blockName)
              .box(box)
              .build();
    }


    /**
     * Imports a single position. (BinderySignaturePlacement)
     * @param placement The position XML to be imported.
     * @param gangJobXml The GangJob XML.
     * @return The Position object.
     * @throws Exception 
     */
    private Position readPosition(DtoFormBinderySignaturePlacement dtoFBSP) throws Exception {
			
			// TODO
			// muss aus Schleife kommen
			//final List<DtoFormBinderySignaturePlacement> lp = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
			//final DtoFormBinderySignaturePlacement dtoFBSP = lp[0];
			
			// TODO
			// Warum wird hier 4 mal trim left genutzt?
//      final Border clip =  new Border(Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()),Math.round(placement.trim.@left.toFloat()));
			final Border clip = new Border(Long.valueOf(Math.round((float)dtoFBSP.getTrim().getLeft())), Long.valueOf(Math.round((float)dtoFBSP.getTrim().getLeft())), Long.valueOf(Math.round((float)dtoFBSP.getTrim().getLeft())), Long.valueOf(Math.round((float)dtoFBSP.getTrim().getLeft())));
			
			
			// TODO
			// Warum float?
			
      // absolute box
//      float llx = placement.offset.@x.toFloat() - placement.trim.@left.toFloat()
			final float llx = (float)(dtoFBSP.getOffset().getX() - dtoFBSP.getTrim().getLeft());
			
//      float lly = placement.offset.@y.toFloat() - placement.trim.@bottom.toFloat()
			final float lly = (float)(dtoFBSP.getOffset().getY() - dtoFBSP.getTrim().getBottom());
			
//      float urx = placement.offset.@x.toFloat() + placement.format.@width.toFloat() + placement.trim.@right.toFloat()
			final float urx = (float)(dtoFBSP.getOffset().getX() + dtoFBSP.getFormat().getWidth() + dtoFBSP.getTrim().getRight());
			
//      float ury = placement.offset.@y.toFloat() + placement.format.@height.toFloat() + placement.trim.@top.toFloat()
			final float ury = (float)(dtoFBSP.getOffset().getY() + dtoFBSP.getFormat().getHeight() + dtoFBSP.getTrim().getTop());


      final Rectangle absoluteBox = new Rectangle(
              DimensionUtil.micro2dtp(llx),
              DimensionUtil.micro2dtp(lly),
              DimensionUtil.micro2dtp(urx),
              DimensionUtil.micro2dtp(ury)
        );

//			Rectangle absoluteBox2 = new Rectangle(
//				DimensionUtil.micro2dtp(llx2),
//				DimensionUtil.micro2dtp(lly2),
//				DimensionUtil.micro2dtp(urx2),
//				DimensionUtil.micro2dtp(ury2)
//			  )
				
      // orientation
      Orientation orientation;
//      String rotation = placement.@rotation.toString()
			final DtoRotation rotation = dtoFBSP.getRotation();

//      if (rotation == "ZERO") {
//          orientation = Orientation.Rotate0
//      } else if (rotation == "CC90") {
//          orientation = Orientation.Rotate90
//      } else if (rotation == "CC180") {
//          orientation = Orientation.Rotate180
//      } else if (rotation == "CC270") {
//          orientation = Orientation.Rotate270
//      } else {
//					Log.error("Rotation '" + rotation + "' is not supported.");
//        	throw new IOException("Rotation '" + rotation + "' is not supported.");
//      }
			
			switch (rotation) {
				case ZERO:
					orientation = Orientation.Rotate0;
					break;
				case CC90:
					orientation = Orientation.Rotate90;
					break;
				case CC180:
					orientation = Orientation.Rotate180;
					break;
				case CC270:
					orientation = Orientation.Rotate270;
					break;
				default:
					log.error("Rotation '" + rotation.toString() + "' is not supported.");
					throw new IOException("Rotation '" + rotation.toString() + "' is not supported.");
			}

      // load bindery signature
//      String binderySignatureId = placement.binderySignatureRef.@id.toString()
			String binderySignatureId = dtoFBSP.getBinderySignatureRef().getId();
//      BinderySignature binderySignature = readBinderySignature(binderySignatureId, placement, gangJobXml, orientation, placement.@flipped.toBoolean())
			// TODO
			// groovy here
    	//BinderySignature binderySignature = readBinderySignature(binderySignatureId, placement, gangJobXml, orientation, placement.@flipped.toBoolean())
			BinderySignature binderySignature = readBinderySignature(binderySignatureId, dtoFBSP, orientation, dtoFBSP.getFlipped());

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
              .build();
    }

    /**
     * Imports a single bindery signature.
     * @param id The identifier of the bindery signature.
     * @param gangJobXml The gangJob xml
     * @return The bindery signature as object.
     * @throws Exception
     */
    private BinderySignature readBinderySignature(String binderySignatureId, DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation, boolean flipped) throws Exception {
        
//			BinderySignature binderySignature = null;
			
			// TODO
			// muss aus Schleife kommen
//			final List<DtoFormBinderySignaturePlacement> lp = dtoGJE.getGangJob().getForm().getPlacementZone().getBinderySignaturePlacements();
//			final DtoFormBinderySignaturePlacement lp0 = lp[0];
			
//			final List<DtoBinderySignature> lp2 = dtoGJE.getGangJob().getBinderySignatures().getBinderySignatures();
//			final DtoBinderySignature lp20 = lp2[0];
			
//			for (DtoBinderySignature bs : lp2) {
//				println(bs.getId());
//			}

      // find bindery signature node
//      def bsXml = gangJobXml.binderySignatures.binderySignature.find { it.@id == id }
			

			DtoBinderySignature bsXml = null;
			final List<DtoBinderySignature> dtoBinderySignatureList = dtoGJE.getGangJob().getBinderySignatures().getBinderySignatures();
			for (DtoBinderySignature bs : dtoBinderySignatureList) {
				if (bs.getId() == binderySignatureId) {
					bsXml = bs;
					break;
				}
			}
			


//      if (bsXml.size() == 1) {

      // bindery signature size
//      float heightDtp = DimensionUtil.micro2dtp(
//				(float) (bsXml.trimFormat.@height.toFloat() + getTrimHead(positionXml, orientation) + getTrimFoot(positionXml, orientation))
//      )
			final float heightDtp = DimensionUtil.micro2dtp(
				(float) ((float) bsXml.getTrimFormat().getHeight() + getTrimHead(dtoFBSP, orientation) + getTrimFoot(dtoFBSP, orientation))
			);
//      float widthDtp = DimensionUtil.micro2dtp(
//        (float) (bsXml.trimFormat.@width.toFloat() + getTrimFace(positionXml, orientation) + getTrimSpine(positionXml, orientation))
//      )
			final float widthDtp = DimensionUtil.micro2dtp(
				(float) ((float) bsXml.getTrimFormat().getWidth() + getTrimFace(dtoFBSP, orientation) + getTrimSpine(dtoFBSP, orientation))
			);

      final XYPair binderySignatureSize = new XYPair(widthDtp, heightDtp);

      // fold catalog
//      String strFoldCatalog = bsXml.signature.signatureTypeRef.@id.toString();
			
			final String strFoldCatalog;
			// TODO
			// groovy compiler does not make this final
			FoldCatalog foldCatalog;
			try {
				strFoldCatalog = bsXml.getSignature().getSignatureTypeRef().getId();
				foldCatalog = FoldCatalog.findByName(strFoldCatalog);
			} catch (NullPointerException e) {
				foldCatalog = FoldCatalog.F2_1;
			}

//      final FoldCatalog foldCatalog;

//      if (StringUtils.isEmpty(strFoldCatalog)) {
//          foldCatalog = FoldCatalog.F2_1;
//      } else {
//          foldCatalog = FoldCatalog.findByName(strFoldCatalog);
//      }

      // bindery signature context
      final Integer bsNumberTotal;
			// TODO
			// cannot make this final according to groovy compiler
			Integer bsNumberCurrent ;
//			Integer bsNumberTotal2 = null;
//			Integer bsNumberCurrent2 = null;

//      if(bsXml.signature.relatedSignatureRefs.size() > 0) {
//					// TODO
//					// Stimmt das hier? Ich aendere es und streiche .ref
//          bsNumberTotal = bsXml.signature.relatedSignatureRefs.ref.size()
//					
//					
//					// TODO
//					// Das hier loeschen
////							ArrayList<Object> al = new ArrayList();
//					
////							for (Object a : bsXml.signature.relatedSignatureRefs.ref) {
////								println(a.toString())
////								al.add(a)
////							}
//
//          // validate index path for collecting only
//          boolean isValid = true
//
//					// TODO
//					// wie wird das schoen ohne groovy?
//          bsXml.signature.relatedSignatureRefs.ref.each {
//              if(!it.@indexPath.toString().matches("0( 0)*")) {
//                  isValid = false
//                  log.warn("IndexPath '" + it.@indexPath + "' is not supported.")
//              }
//          }
//
//          if(isValid) {
//              String signatureId = bsXml.signature.@id
//              String[] indexPath = bsXml.signature.relatedSignatureRefs.ref.find {
//                  it.@binderySignatureRef == signatureId
//              }.@indexPath.toString().split(" ")
//
//              bsNumberCurrent = indexPath.length
//          }
//      }
			
			if(bsXml.getSignature().getRelatedSignatureRefs().size() > 0) {
				bsNumberTotal = bsXml.getSignature().getRelatedSignatureRefs().size();

				// validate index path for collecting only
				boolean isValid = true;
				
				for (DtoSignatureRef dtoSR : bsXml.getSignature().getRelatedSignatureRefs()) {
					final String indexPath = dtoSR.getIndexPath().toString().replaceAll("[", "").replaceAll("]", "").replaceAll(",", "");
					if (!indexPath.matches("0( 0)*")) {
						isValid = false;
						log.warn("IndexPath '" + indexPath + "' is not supported.");
					}
				}

				if(isValid) {
						for (DtoSignatureRef dtoSR : bsXml.getSignature().getRelatedSignatureRefs()) {
							if (dtoSR.getBinderySignatureRef() == bsXml.getSignature().getId()) {
								bsNumberCurrent = dtoSR.getIndexPath().size();
								break;
							}
						}
				}
			}

      // priority
//      Priority priority = Priority.findByValue(bsXml.@priority.toInteger());
			// TODO
			// Should not need to cast to int here
      Priority priority = Priority.findByValue(bsXml.getPriority().intValue());

      // extract signature cells
      final List<SignatureCell> signatureCells;
//      List<SignatureCell> signatureCells2;

      Border innerContentFrame;

			// TODO
			// This could be switch case
      if (FoldCatalog.F2_1 == foldCatalog) {
          signatureCells = new ArrayList<>(1);
          signatureCells.add(createSignatureCellF2(bsXml, dtoFBSP, orientation));

          innerContentFrame = new Border(0L);

      } else if (FoldCatalog.F4_1 == foldCatalog) {
//          signatureCells = new ArrayList<>(bsXml.signature.strippingCells.strippingCell.size())
					signatureCells = new ArrayList<>(bsXml.getSignature().getStrippingCells().size());

          // row index 0 and 1
//          def col_0 = bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }
					final DtoStrippingCell col_0;
					// TODO
					// this variable cannot be final regarding to groovy compiler?
					DtoStrippingCell col_1;
					final boolean gotCol_0;
					// TODO
					// this variable cannot be final regarding to groovy compiler?
					boolean gotCol_1;
					for (DtoStrippingCell dtoSC : bsXml.getSignature().getStrippingCells()) {
						if (dtoSC.getColIndex() == 0) {
							col_0 = dtoSC;
							gotCol_0 = true;
						}
						if (dtoSC.getColIndex() == 1) {
							col_1 = dtoSC;
							gotCol_1 = true;
						}
						if (gotCol_0 && gotCol_1) {
							break;
						}
					}
					
          signatureCells.add(
                  createSignatureCellF4(col_0)
          );

          // row index 1
//          def col_1 = bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "1" }

          signatureCells.add(
                  createSignatureCellF4(col_1)
          );
					
					// TODO
					// round still necessary when using API?
//					Long a = Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@headTrim.toFloat()).longValue();
//					Long b = Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@footTrim.toFloat()).longValue();
//					Long c = Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@faceTrim.toFloat()).longValue();
//					Long d = Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "1" }.@faceTrim.toFloat()).longValue();
					
//					Long a2 = col_0.getHeadTrim();
//					Long b2 = col_0.getFootTrim();
//					Long c2 = col_0.getFaceTrim();
//					Long d2 = col_1.getFaceTrim();

//          innerContentFrame=new Border(
//                  Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@headTrim.toFloat()).longValue(),
//                  Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@footTrim.toFloat()).longValue(),
//                  Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "0" }.@faceTrim.toFloat()).longValue(),
//                  Math.round(bsXml.signature.strippingCells.strippingCell.find { it.@colIndex == "1" }.@faceTrim.toFloat()).longValue());
								
          innerContentFrame=new Border(
          	col_0.getHeadTrim(),
          	col_0.getFootTrim(),
          	col_0.getFaceTrim(),
          	col_1.getFaceTrim());

      } else {
          log.error("Fold Schema is not supported.");
          throw new Exception("FoldSchema is not supported.");
      }

      // create bindery signature object
      final BinderySignature binderySignature = new BinderySignature.Builder()
              .label(bsXml.getLabel())
              .amount(bsXml.getMustDemand())
              .priority(priority)
              .jobId(bsXml.getOrderRef())
              .foldCatalog(foldCatalog)
              .binderySignatureSize(binderySignatureSize)
              .signatureCells(signatureCells)
              .flipped(flipped)
              .bsNumberTotal(bsNumberTotal)
              .bsNumberCurrent(bsNumberCurrent)
              .innerContentFrame(innerContentFrame)
              .build();
//      }

      // return bindery signature object
      return binderySignature;
    }

    /**
     * Import the signature cells for a F2-1 folding scheme bindery signature.
     * @param positionXml The Position (BindeySignaturesPlacement) XML snippet.
     * @return The extracted information of the SignatureCell
     */
    private SignatureCell createSignatureCellF2(DtoBinderySignature bsXml, DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation) {
			
//			// TODO
//			// muss uebergeben werden
//				final DtoBinderySignature bsXml2;
//			final List<DtoBinderySignature> dtoBinderySignatureList = dtoGJE.getGangJob().getBinderySignatures().getBinderySignatures();
//			for (DtoBinderySignature bs : dtoBinderySignatureList) {
//				if (bs.getId() == "768922-115_8-11") {
//					bsXml2 = bs;
//					break;
//				}
//			}

        // bleed
//        float bleedFace = DimensionUtil.micro2dtp(bsXml.bleed.@right.toFloat());
//        float bleedFoot = DimensionUtil.micro2dtp(bsXml.bleed.@bottom.toFloat());
//        float bleedHead = DimensionUtil.micro2dtp(bsXml.bleed.@top.toFloat());
//        float bleedSpine = DimensionUtil.micro2dtp(bsXml.bleed.@left.toFloat());
				
				
				
				final float bleedFace = DimensionUtil.micro2dtp((float) bsXml.getBleed().getRight());
				final float bleedFoot = DimensionUtil.micro2dtp((float) bsXml.getBleed().getBottom());
				final float bleedHead = DimensionUtil.micro2dtp((float) bsXml.getBleed().getTop());
				final float bleedSpine = DimensionUtil.micro2dtp((float) bsXml.getBleed().getLeft());

				
				// TODO
				// here is still groovy maybe
        // trim
        float trimFace = DimensionUtil.micro2dtp(getTrimFace(dtoFBSP, orientation))
        float trimFoot = DimensionUtil.micro2dtp(getTrimFoot(dtoFBSP, orientation))
        float trimHead = DimensionUtil.micro2dtp(getTrimHead(dtoFBSP, orientation))
        float trimSpine = DimensionUtil.micro2dtp(getTrimSpine(dtoFBSP, orientation))

//        XYPair trimSize = new XYPair(
//                DimensionUtil.micro2dtp(bsXml.trimFormat.@width.toFloat()),
//                DimensionUtil.micro2dtp(bsXml.trimFormat.@height.toFloat())
//        )
				
				final XYPair trimSize = new XYPair(
					DimensionUtil.micro2dtp((float) bsXml.getTrimFormat().getWidth()),
					DimensionUtil.micro2dtp((float) bsXml.getTrimFormat().getHeight())
				)

				// TODO
				// positionXml without groovy, see all below
        // pages
//        RunList pageFront = new RunList(
//                positionXml.selectedPrintData.frontPage.@pdfUrl.toString(),
//                positionXml.selectedPrintData.frontPage.@pdfPageNumber.toInteger() - 1
//        )

				final RunList pageFront = new RunList(
//					positionXml.selectedPrintData.frontPage.@pdfUrl.toString(),
					dtoFBSP.getSelectedPrintData().getFrontPage().getPdfUrl(),
//					positionXml.selectedPrintData.frontPage.@pdfPageNumber.toInteger() - 1,
					dtoFBSP.getSelectedPrintData().getFrontPage().getPdfPageNumber() -1
				);
				
        final RunList pageBack;

//        if(StringUtils.isEmpty(positionXml.selectedPrintData.backPage.@pdfUrl.toString())) {
      	if(StringUtils.isEmpty(dtoFBSP.getSelectedPrintData().getBackPage().getPdfUrl())) {
            pageBack = null;
        } else {
            pageBack = new RunList(
//                    positionXml.selectedPrintData.backPage.@pdfUrl.toString(),
							dtoFBSP.getSelectedPrintData().getBackPage().getPdfUrl(),
//                    positionXml.selectedPrintData.backPage.@pdfPageNumber.toInteger() - 1
							dtoFBSP.getSelectedPrintData().getBackPage().getPdfPageNumber() - 1
            );
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
    private static SignatureCell createSignatureCellF4(DtoStrippingCell strippingCell) {

        // bleed
//        float bleedFace = DimensionUtil.micro2dtp(strippingCell.@faceBleed.toFloat())
//        float bleedFoot = DimensionUtil.micro2dtp(strippingCell.@footBleed.toFloat())
//        float bleedHead = DimensionUtil.micro2dtp(strippingCell.@headBleed.toFloat())
//        float bleedSpine = DimensionUtil.micro2dtp(strippingCell.@spineBleed.toFloat())
			
				final float bleedFace = DimensionUtil.micro2dtp((float) strippingCell.getFaceBleed());
				final float bleedFoot = DimensionUtil.micro2dtp((float) strippingCell.getFootBleed());
				final float bleedHead = DimensionUtil.micro2dtp((float) strippingCell.getHeadBleed());
				final float bleedSpine = DimensionUtil.micro2dtp((float) strippingCell.getSpineBleed());

        // trim
//        float trimFace = DimensionUtil.micro2dtp(strippingCell.@faceTrim.toFloat())
//        float trimFoot = DimensionUtil.micro2dtp(strippingCell.@footTrim.toFloat())
//        float trimHead = DimensionUtil.micro2dtp(strippingCell.@headTrim.toFloat())
//        float trimSpine = DimensionUtil.micro2dtp(strippingCell.@spineTrim.toFloat())
				
				float trimFace = DimensionUtil.micro2dtp((float) strippingCell.getFaceTrim());
				final float trimFoot = DimensionUtil.micro2dtp((float) strippingCell.getFootTrim());
				final float trimHead = DimensionUtil.micro2dtp((float) strippingCell.getHeadTrim());
				final float trimSpine = DimensionUtil.micro2dtp((float) strippingCell.getSpineTrim());

//        XYPair trimSize = new XYPair(
//                DimensionUtil.micro2dtp(strippingCell.intrinsicTrimBoxFormat.@width.toFloat()),
//                DimensionUtil.micro2dtp(strippingCell.intrinsicTrimBoxFormat.@height.toFloat())
//        )
				
      	XYPair trimSize = new XYPair(
      		DimensionUtil.micro2dtp((float) strippingCell.getIntrinsicTrimBoxFormat().getWidth()),
      		DimensionUtil.micro2dtp((float) strippingCell.getIntrinsicTrimBoxFormat().getHeight())
    		)

        // creep compensation
//        float creepPercent = strippingCell.@creepCompensation.toFloat()
//				final double test = strippingCell.getCreepCompensation();
				final float creepPercent = (float) strippingCell.getCreepCompensation();
        final float creepAbsolute = trimSize.getX() * (1 - creepPercent);

        trimSize = new XYPair(
                (float) (trimSize.getX() - creepAbsolute),
                trimSize.getY()
        )

        trimFace += creepAbsolute;

        // pages
        final RunList pageFront = new RunList(
//                strippingCell.frontPage.printData.@pdfUrl.toString(),
					strippingCell.getFrontPage().getPrintData().getPdfUrl(),
//                strippingCell.frontPage.printData.@pdfPageNumber.toInteger() - 1
					strippingCell.getFrontPage().getPrintData().getPdfPageNumber() -1
        )
        final RunList pageBack = new RunList(
//                strippingCell.backPage.printData.@pdfUrl.toString(),
					strippingCell.getBackPage().getPrintData().getPdfUrl(),
//                strippingCell.backPage.printData.@pdfPageNumber.toInteger() - 1
					strippingCell.getBackPage().getPrintData().getPdfPageNumber() - 1
        )
//        int pageIndexFront = strippingCell.frontPage.@index.toInteger()
				final int pageIndexFront = strippingCell.getFrontPage().getIndex();
//        int pageIndexBack = strippingCell.backPage.@index.toInteger()
				final int pageIndexBack = strippingCell.getBackPage().getIndex();

        // orientation
				// TODO
				// groovy compiler does not allow this as final
        Orientation orientation

//        if ("DOWN".equals(strippingCell.@orientation.toString())) {
//            orientation = Orientation.Rotate180
//
//        } else if ("LEFT".equals(strippingCell.@orientation.toString())) {
//            orientation = Orientation.Rotate90
//
//        } else if ("RIGHT".equals(strippingCell.@orientation.toString())) {
//            orientation = Orientation.Rotate270
//
//        } else {
//            orientation = Orientation.Rotate0
//        }
				
				DtoOrientation test = strippingCell.getOrientation();
				switch (strippingCell.getOrientation()) {
					case DtoOrientation.DOWN:
						orientation = Orientation.Rotate180;
						break;
					case  DtoOrientation.LEFT:
						orientation = Orientation.Rotate90;
						break;
					case  DtoOrientation.RIGHT:
						orientation = Orientation.Rotate270;
						break;
					default:
						orientation = Orientation.Rotate0;
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

    private static float getTrimFace(DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation) {
//			DtoBinderySignature bsXml, DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation
			
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
//            trimValue = positionXml.trim.@right.toFloat()
						trimValue = (float) dtoFBSP.getTrim().getRight();

        } else if (Orientation.Rotate90 == orientation) {
//            trimValue = positionXml.trim.@bottom.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getBottom();

        } else if (Orientation.Rotate180 == orientation) {
//            trimValue = positionXml.trim.@left.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getLeft();

        } else if (Orientation.Rotate270 == orientation) {
//            trimValue = positionXml.trim.@top.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getTop();

        }

        return trimValue;
    }

    private static float getTrimHead(DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
//            trimValue = positionXml.trim.@top.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getTop();

        } else if (Orientation.Rotate90 == orientation) {
//            trimValue = positionXml.trim.@right.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getRight();

        } else if (Orientation.Rotate180 == orientation) {
//            trimValue = positionXml.trim.@bottom.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getBottom();

        } else if (Orientation.Rotate270 == orientation) {
//            trimValue = positionXml.trim.@left.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getLeft();

        }

        return trimValue;
    }

    private static float getTrimSpine(DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
//            trimValue = positionXml.trim.@left.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getLeft();

        } else if (Orientation.Rotate90 == orientation) {
//            trimValue = positionXml.trim.@top.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getTop();

        } else if (Orientation.Rotate180 == orientation) {
//            trimValue = positionXml.trim.@right.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getRight();

        } else if (Orientation.Rotate270 == orientation) {
//            trimValue = positionXml.trim.@bottom.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getBottom();

        }

        return trimValue;
    }

    private static float getTrimFoot(DtoFormBinderySignaturePlacement dtoFBSP, Orientation orientation) {
        float trimValue = 0

        if (Orientation.Rotate0 == orientation) {
//            trimValue = positionXml.trim.@bottom.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getBottom();

        } else if (Orientation.Rotate90 == orientation) {
//            trimValue = positionXml.trim.@left.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getLeft();

        } else if (Orientation.Rotate180 == orientation) {
//            trimValue = positionXml.trim.@top.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getTop();

        } else if (Orientation.Rotate270 == orientation) {
//            trimValue = positionXml.trim.@right.toFloat()
					trimValue = (float) dtoFBSP.getTrim().getRight();
        }

        return trimValue;
    }
}
