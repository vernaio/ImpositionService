package de.perfectpattern.print.imposition.model;


import de.perfectpattern.print.imposition.model.type.Priority;
import de.perfectpattern.print.imposition.model.type.Rectangle;
import de.perfectpattern.print.imposition.model.type.WorkStyle;

import java.util.Collections;
import java.util.List;

/**
 * Sheet object class - an aggregation of XJDF Layout, XJDF ColorantControl etc.
 */
public class Sheet {
    private final String sheetId;
    private final String layoutTaskId;
    private final Rectangle surfaceContentsBox; // surface content box is Bleed Unware (always 0 0)
    private final WorkStyle workStyle;
    private final List<Position> positions;
    private final List<CutBlock> cuttingParams;
    private final float bleed;
    private final int amount;
    private final Priority priority;

    /**
     * Default constructor.
     */
    private Sheet(Builder builder) {
        this.sheetId = builder.sheetId;
        this.layoutTaskId = builder.layoutTaskId;
        this.surfaceContentsBox = builder.surfaceContentsBox;
        this.workStyle = builder.workStyle;
        this.positions = builder.positions;
        this.cuttingParams = builder.cuttingParams;
        this.bleed = builder.bleed;
        this.amount = builder.amount;

        // compute values
        this.priority = definePriority(builder.positions);
    }

    public String getSheetId() {
        return sheetId;
    }

    public String getLayoutTaskId() {
        return layoutTaskId;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public List<CutBlock> getCuttingParams() {
        return cuttingParams;
    }

    public Rectangle getSurfaceContentsBox() {
        return surfaceContentsBox;
    }

    public WorkStyle getWorkStyle() {
        return workStyle;
    }

    public float getBleed() {
        return bleed;
    }

    public int getAmount() {
        return amount;
    }

    public Priority getPriority() {
        return priority;
    }

    public Rectangle getMediaBox() {
        return new Rectangle(0,0,surfaceContentsBox.getUrx() + 2 * bleed, surfaceContentsBox.getUry() + 2 * bleed);
    }

    public Rectangle getTrimBox() {
        return new Rectangle(bleed,bleed,surfaceContentsBox.getUrx() + bleed, surfaceContentsBox.getUry() + bleed);
    }

    /**
     * Define the leading priority from a set of positions.
     * @param positions The list of positions.
     * @return The dominating priority
     */
    private Priority definePriority(List<Position> positions) {
        int value = -1;

        if(positions != null) {
            for (Position pos : positions) {
                if (pos.getBinderySignature() != null && pos.getBinderySignature().getPriority().getValue() > value) {
                    value = pos.getBinderySignature().getPriority().getValue();
                }
            }
        }

        return Priority.findByValue(value);
    }


    /**
     * Sheet builder class.
     */
    public static class Builder {
        private String sheetId;
        private String layoutTaskId;
        private Rectangle surfaceContentsBox;
        private WorkStyle workStyle;
        private List<Position> positions;
        private List<CutBlock> cuttingParams;
        private float bleed;
        private int amount;

        /**
         * Default constructor.
         */
        public Builder() {
        }

        /**
         * Custom constructor, accepting a sheet object for initializing.
         * @param sheet The sheet object for initializing.
         */
        public Builder(Sheet sheet) {
            this.sheetId = sheet.getSheetId();
            this.layoutTaskId = sheet.getLayoutTaskId();
            this.surfaceContentsBox = sheet.getSurfaceContentsBox();
            this.workStyle = sheet.getWorkStyle();
            this.positions = sheet.getPositions();
            this.cuttingParams = sheet.getCuttingParams();
            this.bleed = sheet.getBleed();
            this.amount = sheet.getAmount();
        }

        public Builder sheetId(String sheetId) {
            this.sheetId = sheetId;
            return this;
        }

        public Builder layoutTaskId(String layoutTaskId) {
            this.layoutTaskId = layoutTaskId;
            return this;
        }

        public Builder surfaceContentsBox(Rectangle surfaceContentsBox) {
            this.surfaceContentsBox = surfaceContentsBox;
            return this;
        }

        public Builder workStyle (WorkStyle workStyle) {
            this.workStyle = workStyle;
            return this;
        }

        public Builder positions(List<Position> positions) {
            this.positions = Collections.unmodifiableList(positions);
            return this;
        }

        public Builder cuttingParams(List<CutBlock> cuttingParams) {
            this.cuttingParams = Collections.unmodifiableList(cuttingParams);
            return this;
        }

        public Builder bleed(float bleed) {
            this.bleed = bleed;
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Sheet build() {

            // validate
            if(this.surfaceContentsBox != null) {
                if(this.surfaceContentsBox.getLlx() != 0 || this.surfaceContentsBox.getLly() != 0) {
                    throw new IllegalArgumentException("SurfaceContentBox must be on origin (0,0 ux, uy)");
                }
            }

            // create new sheet
            return new Sheet(this);
        }
    }
}
