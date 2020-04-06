package de.perfectpattern.print.imposition.model;

import de.perfectpattern.print.imposition.model.type.Rectangle;

/**
 * XJDF CutBlock model.
 */
public class CutBlock {

    private final Rectangle box;
    private final String blockName;

    /**
     * Private constructor. Accepting a builder object for initializing.
     * @param builder The builder object.
     */
    private CutBlock(Builder builder) {
        this.box = builder.box;
        this.blockName = builder.blockName;
    }

    public Rectangle getBox() {
        return box;
    }

    public String getBlockName() {
        return blockName;
    }

    /**
     * XJDF CutBlock builder class.
     */
    public static class Builder {
        private Rectangle box;
        private String blockName;

        /**
         * Default constructor.
         */
        public Builder() {

        }

        public Builder box(Rectangle box) {
            this.box = box;
            return this;
        }

        public Builder blockName(String blockName) {
            this.blockName = blockName;
            return this;
        }

        public CutBlock build() {
            return new CutBlock(this);
        }
    }
}
