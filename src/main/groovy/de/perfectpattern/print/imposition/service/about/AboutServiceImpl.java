package de.perfectpattern.print.imposition.service.about;

import com.lowagie.text.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * Implementation of the about service interface.
 */
@Controller
public class AboutServiceImpl implements AboutService {

    @Value("${app.name}")
    private String appName;

    @Value("${git.build.version}")
    private String version;

    @Value("${git.commit.time}")
    private String buildTime;

    @Value("${git.commit.id}")
    private String commitId;

    @Value("${git.commit.id.abbrev}")
    private String commitIdAbbrev;

    @Override
    public String getPdfLibrary() {
        return Document.getVersion().trim();
    }

    @Override
    public String getVersion() {
        return version.trim();
    }

    @Override
    public String getAppName() {
        return appName.trim();
    }

    @Override
    public String getBuildTime() {
        return buildTime.trim();
    }

    @Override
    public String getCommitId() {
        return commitId.trim();
    }

    @Override
    public String getCommitIdAbbrev() {
        return commitIdAbbrev.trim();
    }
}
