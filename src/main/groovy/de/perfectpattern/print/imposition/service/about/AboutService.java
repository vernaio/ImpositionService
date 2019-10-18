package de.perfectpattern.print.imposition.service.about;

/**
 * Service interface providing details about the current library.
 */
public interface AboutService {

    /**
     * Returns the name of the PDF Engine.
     * @return The PDF Engines name.
     */
    String getPdfLibrary();

    /**
     * Returns the version of the application.
     * @return The applications version.
     */
    String getVersion();

    /**
     * Returns the name of the application.
     * @return The applications name.
     */
    String getAppName();

    /**
     * Retruns the build time of the application.
     * @return The applications build time.
     */
    String getBuildTime();

    /**
     * Returns the full commit id of the latest change.
     * @return The latest full commit id.
     */
    String getCommitId();

    /**
     * Returns the shortend commit id of the latest change.
     * @return The latest shortend commit id.
     */
    String getCommitIdAbbrev();

}
