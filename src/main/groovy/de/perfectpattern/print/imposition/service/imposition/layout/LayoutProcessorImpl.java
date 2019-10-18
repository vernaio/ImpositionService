package de.perfectpattern.print.imposition.service.imposition.layout;

import com.lowagie.text.pdf.PdfReader;
import de.perfectpattern.print.imposition.exception.CorruptPdfException;
import de.perfectpattern.print.imposition.model.*;
import de.perfectpattern.print.imposition.model.type.*;
import de.perfectpattern.print.imposition.service.imposition.layout.label.*;
import de.perfectpattern.print.imposition.service.imposition.layout.object.PlacedObject;
import de.perfectpattern.print.imposition.service.imposition.layout.object.content.ContentObject;
import de.perfectpattern.print.imposition.service.imposition.layout.object.mark.BinderySignatureInfoMark;
import de.perfectpattern.print.imposition.service.imposition.layout.object.mark.BoxMark;
import de.perfectpattern.print.imposition.service.imposition.layout.object.mark.CutMark;
import de.perfectpattern.print.imposition.util.DimensionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.*;

import static de.perfectpattern.print.imposition.model.type.Anchor.*;
import static de.perfectpattern.print.imposition.util.DimensionUtil.mm2dtp;

@Controller
public class LayoutProcessorImpl implements LayoutProcessor {

    @Value("${data.root}")
    private String dataRoot;

    @Value("${MARK_BS_INFO}")
    private String markBsInfo;

    @Override
    public Layout generateLayout(Sheet sheet) throws IOException {
        Layout layout = new Layout();
        HashMap<String, PdfReader> pdfReaderCache = new HashMap<>(100);

        // labels
        layout.setLabels(createLabels(sheet));

        // normalize positions (aware of sheet bleed)
        List<Position> normalizedPositions = new ArrayList<>(sheet.getPositions().size());

        for(Position position: sheet.getPositions()) {
            normalizedPositions.add(
                new Position.Builder(position).absoluteBox(
                    new Rectangle(
                    position.getAbsoluteBox().getLlx() + sheet.getBleed(),
                    position.getAbsoluteBox().getLly() + sheet.getBleed(),
                    position.getAbsoluteBox().getUrx() + sheet.getBleed(),
                    position.getAbsoluteBox().getUry() + sheet.getBleed()
                    )
                ).build()
            );
        }

        // front surface
        layout.setPlacedObjectsFront(new ArrayList<>(100));

        for (Position position : normalizedPositions) {
            Rectangle absoluteBox = position.getAbsoluteBox();

            // place bindery signature
            layout.getPlacedObjectsFront().addAll(
                    placePosition(position.getBinderySignature(), position.getOrientation(), absoluteBox, Side.Front, pdfReaderCache)
            );

            // place bindery signature info marks
            layout.getPlacedObjectsFront().addAll(
                placePositionMarks(position.getBinderySignature(), position.getOrientation(), absoluteBox, Side.Front)
            );
        }

        // back surface
        layout.setPlacedObjectsBack(new ArrayList<>(100));

        for (Position position : normalizedPositions) {
            Rectangle reversedAbsoluteBox = reverseObject(position.getAbsoluteBox(), sheet.getMediaBox());
            Orientation reversedOrientation = revertOrientation(position.getOrientation());

            // place bindery signature
            layout.getPlacedObjectsBack().addAll(
            	placePosition(position.getBinderySignature(), reversedOrientation, reversedAbsoluteBox, Side.Back, pdfReaderCache)
            );

            // place bindery signature info marks
            layout.getPlacedObjectsBack().addAll(
                placePositionMarks(position.getBinderySignature(), reversedOrientation, reversedAbsoluteBox, Side.Back)
            );
        }

        return layout;
    }

    /**
     * Helper method for placing bindery signature relveant marks.
     */
    private List<PlacedObject> placePositionMarks(BinderySignature binderySignature, Orientation orientation, Rectangle absoluteBox, Side side) {
        List<PlacedObject> result = new ArrayList<>(10);

        // if("TRUE" == markBsInfo) {
            if (FoldCatalog.F4_1 == binderySignature.getFoldCatalog()) {
                if (binderySignature.getSignatureCells().get(0).getTrimFoot() > mm2dtp(2.0f)) {
                    result.add(new BinderySignatureInfoMark(absoluteBox, orientation, binderySignature, side));
                }
            }
        // }

        return result;
    }

    /**
     * Defines where to put which labels on the sheet.
     * @param sheet The sheet to be labeled.
     * @return Map of labels.
     */
    private Map<Anchor, List<AbstractLabel>> createLabels(Sheet sheet) {
        Map<Anchor, List<AbstractLabel>> map = new HashMap<>(4);
        List<AbstractLabel> labels;

        // top left
        labels = new ArrayList<>(5);
        map.put(TopLeft, labels);
        labels.add(new SpaceLabel(mm2dtp(50)));
        labels.add(new SheetIdLabel(sheet.getSheetId()));
        labels.add(new PriorityLabel(sheet.getPriority()));
        labels.add(new AmountLabel(sheet.getAmount()));

        // top right
        labels = new ArrayList<>(5);
        map.put(TopRight, labels);
        labels.add(new SpaceLabel(mm2dtp(50)));
        labels.add(new BarcodeLabel(sheet.getSheetId()));

        // bottom left
        labels = new ArrayList<>(5);
        map.put(BottomLeft, labels);
        labels.add(new SpaceLabel(mm2dtp(20)));
        labels.add(new LayMarkLabel());
        labels.add(new SpaceLabel(mm2dtp(15)));
        labels.add(new DebugLabel(sheet));
        // labels.add(new PullMarkLabel());
        //  labels.add(new PullMarkProLabel());

        // bottom right
        labels = new ArrayList<>(5);
        map.put(BottomRight, labels);
        labels.add(new SpaceLabel(mm2dtp(50)));
        labels.add(new SheetSizeLabel(sheet.getTrimBox()));

        // return map
        return map;
    }

    /**
     * Helper method for resolving positions into content objects.
     *
     * @param binderySignature The bindery signature to be resolved.
     * @param orientation      The orientation of the bindery signature.
     * @param absoluteBox      The exact position of the bindery signature (=clipbox)
     * @param side             The side to be imposed.
     * @return A list of placed objects.
     */
    private List<PlacedObject> placePosition(BinderySignature binderySignature, Orientation orientation, Rectangle absoluteBox, Side side, HashMap<String, PdfReader> pdfReaderCache) throws IOException {

        // validate supported fold catalogs
        if (FoldCatalog.F2_1 != binderySignature.getFoldCatalog() &&
                FoldCatalog.F4_1 != binderySignature.getFoldCatalog()) {
            throw new IOException("The following FoldCatalog is not supported: " + binderySignature.getFoldCatalog());
        }

        List<PlacedObject> result = new ArrayList<>(10);


        XYPair bsSize = binderySignature.getBinderySignatureSize();
        XYPair bsAnchor = new XYPair(absoluteBox.getLlx(), absoluteBox.getLly());
        Matrix bsCtm = new Matrix(orientation, bsSize.getX(), bsSize.getY());

        // back page is reversed
        List<SignatureCell> cells = new ArrayList<>(binderySignature.getSignatureCells());

        if (Side.Back == side) {
            Collections.reverse(cells);
        }

        // process cells
        float posX = 0;

        for (SignatureCell sc : cells) {

            // define spine (left or right page)
            int pageIndex = Side.Front == side ? sc.getPageIndexFront() : sc.getPageIndexBack();

            // clip box
            float clipBoxLlx = absoluteBox.getLlx() + posX;
            float clipBoxLly = absoluteBox.getLly();
            float clipBoxUrx = clipBoxLlx + sc.getTrimSpine() + sc.getTrimFace() + sc.getTrimSize().getX();
            float clipBoxUry = clipBoxLly + sc.getTrimFoot() + sc.getTrimHead() + sc.getTrimSize().getY();

            // trim box
            float trimBoxLlx, trimBoxUrx;

            if (pageIndex % 2 == 1) {
                trimBoxLlx = clipBoxLlx + sc.getTrimFace();
                trimBoxUrx = clipBoxUrx - sc.getTrimSpine();
            } else {
                trimBoxLlx = clipBoxLlx + sc.getTrimSpine();
                trimBoxUrx = clipBoxUrx - sc.getTrimFace();
            }

            float trimBoxLly = clipBoxLly + sc.getTrimFoot();
            float trimBoxUry = clipBoxUry - sc.getTrimHead();

            // define and transform boxes on sheet
            Rectangle clipBox = new Rectangle(clipBoxLlx, clipBoxLly, clipBoxUrx, clipBoxUry);
            Rectangle trimBox = new Rectangle(trimBoxLlx, trimBoxLly, trimBoxUrx, trimBoxUry);

            clipBox = DimensionUtil.transform(clipBox, bsCtm, bsAnchor);
            trimBox = DimensionUtil.transform(trimBox, bsCtm, bsAnchor);

            // define orientation
            Orientation orientationCell;

            if (Side.Front == side) {
                orientationCell = Orientation.findByDegree(
                        orientation.getDegree() + sc.getOrientation().getDegree()
                );
            } else {
                orientationCell = Orientation.findByDegree(
                        orientation.getDegree() - sc.getOrientation().getDegree()
                );
            }


            // create content object
            RunList runList = Side.Front == side ? sc.getPageFront() : sc.getPageBack();

            // cache pdf reader
            PdfReader pdfReader = null;

            if (runList != null) {
                Path pdfPath = Paths.get(dataRoot).resolve(runList.getFile().toString());

                if (pdfPath == null || pdfPath.toString().equals("")) {
                    throw new IOException("PDF FileSpec is empty.");
                } else if (!pdfPath.toFile().exists()) {
                    throw new IOException("PDF '" + pdfPath.toString() + "' does not exist.");
                }

                if(!pdfReaderCache.containsKey(pdfPath.toString())) {
                    try {
                        pdfReaderCache.put(pdfPath.toString(), new PdfReader(pdfPath.toUri().toURL()));
                    } catch (Exception ex) {
                        throw new CorruptPdfException("Error reading PDF '" + pdfPath.toUri().toURL()  + "'. PDF may be corrupt.", ex);
                    }
                }

                pdfReader = pdfReaderCache.get(pdfPath.toString());
            }


            // create content object
            result.add(
                    new ContentObject(
                            clipBox,
                            trimBox,
                            orientationCell,
                            pdfReader,
                            runList == null ? 0 : runList.getPage()
                    )
            );

            // draw marks
            if (FoldCatalog.F2_1 == binderySignature.getFoldCatalog()) {
                result.add(
                        new CutMark(CutMark.Type.UpperLeft, trimBox.getLlx(), trimBox.getUry())
                );
                result.add(
                        new CutMark(CutMark.Type.UpperRight, trimBox.getUrx(), trimBox.getUry())
                );
                result.add(
                        new CutMark(CutMark.Type.LowerLeft, trimBox.getLlx(), trimBox.getLly())
                );
                result.add(
                        new CutMark(CutMark.Type.LowerRight, trimBox.getUrx(), trimBox.getLly())
                );
            }

            posX = posX + sc.getTrimSpine() + sc.getTrimSize().getX() + sc.getTrimFace();
        }

        // draw bindery signature box mark (position)
        if (FoldCatalog.F2_1 != binderySignature.getFoldCatalog()) {
            result.add(
                    new CutMark(CutMark.Type.UpperLeft, absoluteBox.getLlx(), absoluteBox.getUry())
            );
            result.add(
                    new CutMark(CutMark.Type.UpperRight, absoluteBox.getUrx(), absoluteBox.getUry())
            );
            result.add(
                    new CutMark(CutMark.Type.LowerLeft, absoluteBox.getLlx(), absoluteBox.getLly())
            );
            result.add(
                    new CutMark(CutMark.Type.LowerRight, absoluteBox.getUrx(), absoluteBox.getLly())
            );
        }

        result.add(
                new BoxMark(
                        absoluteBox,
                        Color.MAGENTA
                )
        );

        // return result
        return result;
    }

    /**
     * Returns the reversed position of an object.
     *
     * @param rectangle The rectangle to be reversed.
     * @return The reversed rectangle
     */
    private Rectangle reverseObject(Rectangle rectangle, Rectangle surfaceContentsBox) {

        return new Rectangle(
                surfaceContentsBox.getWidth() - rectangle.getUrx(),
                rectangle.getLly(),
                surfaceContentsBox.getWidth() - rectangle.getLlx(),
                rectangle.getUry()
        );
    }

    private Orientation revertOrientation(Orientation orientation) {
        Orientation result;

        if (Orientation.Rotate90 == orientation) {
            result = Orientation.Rotate270;

        } else if (Orientation.Rotate270 == orientation) {
            result = Orientation.Rotate90;

        } else {
            result = orientation;
        }

        return result;
    }
}
