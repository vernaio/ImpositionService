package de.perfectpattern.print.imposition;

import de.perfectpattern.print.imposition.service.about.AboutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;


@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private AboutService aboutService;

    /**
     * Application main entrance point.
     *
     * @param args Application parameters.
     */
    public static void main(String[] args) {
        // start app
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    /**
     * Event is called after applications start up.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartUp() {
        log.warn(String.format("%s %s has started. (rev: %s)", aboutService.getAppName(), aboutService.getVersion(), aboutService.getCommitIdAbbrev()));
        log.info("PDF-Engine: " + aboutService.getPdfLibrary());
    }

    /**
     * Read git properties.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }


}
