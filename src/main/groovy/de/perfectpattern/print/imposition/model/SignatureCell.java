package de.perfectpattern.print.imposition.model;

import de.perfectpattern.print.imposition.model.type.Orientation;
import de.perfectpattern.print.imposition.model.type.XYPair;

public class SignatureCell {

    private final RunList pageFront;
    private final RunList pageBack;
    private final int pageIndexFront;
    private final int pageIndexBack;

    private final Orientation orientation;

    private final float bleedFoot;
    private final float bleedFace;
    private final float bleedSpine;
    private final float bleedHead;

    private final float trimFoot;
    private final float trimFace;
    private final float trimSpine;
    private final float trimHead;

    private final XYPair trimSize;

    /**
     * Private constructor.
     */
    private SignatureCell(Builder builder) {
        this.pageFront = builder.pageFront;
        this.pageBack = builder.pageBack;
        this.pageIndexFront = builder.pageIndexFront;
        this.pageIndexBack = builder. pageIndexBack;
        this.orientation = builder.orientation;
        this.bleedFoot = builder.bleedFoot;
        this.bleedFace = builder.bleedFace;
        this.bleedSpine = builder.bleedSpine;
        this.bleedHead = builder.bleedHead;
        this.trimFoot = builder.trimFoot;
        this.trimFace = builder.trimFace;
        this.trimSpine = builder.trimSpine;
        this.trimHead = builder.trimHead;
        this.trimSize = builder.trimSize;
    }

    public RunList getPageFront() {
        return pageFront;
    }

    public RunList getPageBack() {
        return pageBack;
    }

    public int getPageIndexFront() {
        return pageIndexFront;
    }

    public int getPageIndexBack() {
        return pageIndexBack;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public float getBleedFoot() {
        return bleedFoot;
    }

    public float getBleedFace() {
        return bleedFace;
    }

    public float getBleedSpine() {
        return bleedSpine;
    }

    public float getBleedHead() {
        return bleedHead;
    }

    public float getTrimFoot() {
        return trimFoot;
    }

    public float getTrimFace() {
        return trimFace;
    }

    public float getTrimSpine() {
        return trimSpine;
    }

    public float getTrimHead() {
        return trimHead;
    }

    public XYPair getTrimSize() {
        return trimSize;
    }

    /**
     * The SignatureCell builder class.
     */
    public static class Builder {
        private RunList pageFront;
        private RunList pageBack;
        private int pageIndexFront;
        private int pageIndexBack;
        private Orientation orientation;
        private float bleedFoot;
        private float bleedFace;
        private float bleedSpine;
        private float bleedHead;
        private float trimFoot;
        private float trimFace;
        private float trimSpine;
        private float trimHead;
        private XYPair trimSize;

        public Builder pageFront(RunList pageFront) {
            this.pageFront = pageFront;
            return this;
        }

        public Builder pageBack(RunList pageBack) {
            this.pageBack = pageBack;
            return this;
        }

        public Builder pageIndexFront(int pageIndexFront) {
            this.pageIndexFront = pageIndexFront;
            return this;
        }

        public Builder pageIndexBack(int pageIndexBack) {
            this.pageIndexBack = pageIndexBack;
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder bleedFoot(float bleedFoot) {
            this.bleedFoot = bleedFoot;
            return this;
        }

        public Builder bleedFace(float bleedFace) {
            this.bleedFace = bleedFace;
            return this;
        }

        public Builder bleedSpine(float bleedSpine) {
            this.bleedSpine = bleedSpine;
            return this;
        }

        public Builder bleedHead(float bleedHead) {
            this.bleedHead = bleedHead;
            return this;
        }

        public Builder trimFoot(float trimFoot) {
            this.trimFoot = trimFoot;
            return this;
        }

        public Builder trimFace(float trimFace) {
            this.trimFace = trimFace;
            return this;
        }

        public Builder trimSpine(float trimSpine) {
            this.trimSpine = trimSpine;
            return this;
        }

        public Builder trimHead(float trimHead) {
            this.trimHead = trimHead;
            return this;
        }

        public Builder trimSize(XYPair trimSize) {
            this.trimSize = trimSize;
            return this;
        }

        public SignatureCell build() {
            return new SignatureCell(this);
        }
    }
}
