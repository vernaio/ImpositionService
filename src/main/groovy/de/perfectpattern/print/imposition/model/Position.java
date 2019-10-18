package de.perfectpattern.print.imposition.model;

import de.perfectpattern.print.imposition.model.type.Orientation;
import de.perfectpattern.print.imposition.model.type.Rectangle;

/**
 * Position object object - a subset of XJDF Position.
 */
public class Position {

    private final Rectangle absoluteBox;

    private final Orientation orientation;

    private final BinderySignature binderySignature;

    /**
     * Private constructor.
     */
    private Position(Builder builder) {
        this.absoluteBox = builder.absoluteBox;
        this.orientation = builder.orientation;
        this.binderySignature = builder.binderySignature;
    }

    public Rectangle getAbsoluteBox() {
        return absoluteBox;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public BinderySignature getBinderySignature() {
        return binderySignature;
    }

    /**
     * Position builder class.
     */
    public static class Builder {

        private Rectangle absoluteBox;
        private Orientation orientation;
        private BinderySignature binderySignature;

        /**
         * Default constructor.
         */
        public Builder() {

        }

        /**
         * Custom constructor, accepting a bindery signature for initialize.
         * @param position The bindery signature.
         */
        public Builder(Position position) {
            this.absoluteBox = position.getAbsoluteBox();
            this.orientation = position.getOrientation();
            this.binderySignature = position.getBinderySignature();
        }

        public Builder absoluteBox(Rectangle absoluteBox) {
            this.absoluteBox = absoluteBox;
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder binderySignature(BinderySignature binderySignature) {
            this.binderySignature = binderySignature;
            return this;
        }

        public Position build() {
            return new Position(this);
        }
    }
}
