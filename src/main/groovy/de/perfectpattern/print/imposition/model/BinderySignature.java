package de.perfectpattern.print.imposition.model;

import de.perfectpattern.print.imposition.model.type.FoldCatalog;
import de.perfectpattern.print.imposition.model.type.Priority;
import de.perfectpattern.print.imposition.model.type.XYPair;

import java.util.Collections;
import java.util.List;

/**
 * Position object object - a subset of XJDF BinderySignature.
 */
public class BinderySignature {

    private final XYPair binderySignatureSize;
    private final FoldCatalog foldCatalog;
    private final List<SignatureCell> signatureCells;
    private final String label;
    private final int amount;
    private final Priority priority;
    private final String jobId;
    private final boolean flipped;
    private final Integer bsNumberTotal;
    private final Integer bsNumberCurrent;


    /**
     * Private constructor accessible from the builder.
     * @param builder The builder.
     */
    private BinderySignature(Builder builder) {
        this.binderySignatureSize = builder.binderySignatureSize;
        this.signatureCells = builder.signatureCells;
        this.foldCatalog = builder.foldCatalog;
        this.label = builder.label;
        this.amount = builder.amount;
        this.priority = builder.priority;
        this.jobId = builder.jobId;
        this.flipped = builder.flipped;
        this.bsNumberTotal = builder.bsNumberTotal;
        this.bsNumberCurrent = builder.bsNumberCurrent;
    }

    public XYPair getBinderySignatureSize() {
        return binderySignatureSize;
    }

    public List<SignatureCell> getSignatureCells() {
        return signatureCells;
    }

    public FoldCatalog getFoldCatalog() {
        return foldCatalog;
    }

    public String getLabel() {
        return label;
    }

    public int getAmount() {
        return amount;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getJobId() {
        return jobId;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public Integer getBsNumberTotal() {
        return bsNumberTotal;
    }

    public Integer getBsNumberCurrent() {
        return bsNumberCurrent;
    }

    /**
     * BinderySignature builder class.
     */
    public static class Builder {

        private XYPair binderySignatureSize;
        private List<SignatureCell> signatureCells;
        private FoldCatalog foldCatalog;
        private String label;
        private int amount;
        private Priority priority;
        private String jobId;
        private boolean flipped;
        private Integer bsNumberTotal;
        private Integer bsNumberCurrent;

        /**
         * Default constructor.
         */
        public Builder() {
        }

        public Builder binderySignatureSize(XYPair binderySignatureSize) {
            this.binderySignatureSize = binderySignatureSize;
            return this;
        }

        public Builder signatureCells(List<SignatureCell> signatureCells) {
            this.signatureCells = Collections.unmodifiableList(signatureCells);
            return this;
        }

        public Builder foldCatalog(FoldCatalog foldCatalog) {
            this.foldCatalog = foldCatalog;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder amount(int amount)  {
            this.amount = amount;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder flipped(boolean flipped) {
            this.flipped = flipped;
            return this;
        }

        public Builder bsNumberTotal(Integer bsNumberTotal) {
            this.bsNumberTotal = bsNumberTotal;
            return this;
        }

        public Builder bsNumberCurrent(Integer bsNumberCurrent) {
            this.bsNumberCurrent = bsNumberCurrent;
            return this;
        }

        public BinderySignature build() {
            return new BinderySignature(this);
        }
    }
}
