package de.perfectpattern.print.imposition.model;

import java.nio.file.Path;

public class RunList {

    /**
     * Path / URL to the PDL document.
     */
    private final String file;

    /**
     * Zero-based page number in the PDL document.
     */
    private final int page;

    /**
     * Custom constructor.
     * @param file The path to the file.
     * @param page The (zero-based) page in the file.
     */
    public RunList(String file, int page) {
        this.file = file;
        this.page = page;
    }

    public String getFile() {
        return file;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "RunList{" +
                "file=" + file +
                ", page=" + page +
                '}';
    }
}
