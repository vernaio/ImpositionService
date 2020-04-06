package de.perfectpattern.print.imposition.controller;

import de.perfectpattern.print.imposition.service.about.AboutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private AboutService aboutService;

    @RequestMapping(value = "/version", method = RequestMethod.GET, produces="application/json")
    public Version version() {
        return new Version();
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public void ping() {
        log.info("Ping... :)");
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces="application/json")
    public Status status() {
        return new Status();
    }

    /**
     * Private model class for status.
     */
    private class Status {

        private final String status = "UP";

        public String getStatus() {
            return status;
        }
    }

    /**
     * Private model class for version
     */
    private class Version {

        private final String appName;
        private final String appVersion;
        private final String buildTime;
        private final String commitId;
        private final String commitIdAbbrev;

        private Version() {
            this.appName = aboutService.getAppName();
            this.appVersion = aboutService.getVersion();
            this.buildTime = aboutService.getBuildTime();
            this.commitId = aboutService.getCommitId();
            this.commitIdAbbrev = aboutService.getCommitIdAbbrev();
        }

        public String getAppName() {
            return appName;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public String getCommitId() {
            return commitId;
        }

        public String getCommitIdAbbrev() {
            return commitIdAbbrev;
        }
    }
}
