package ca.etsmtl.taf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.io.File;

@Component
public class GatlingConfigurator implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(GatlingConfigurator.class);
    //private static final File SYSTEM_TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
    //private static final File GATLING_RESULTS_FOLDER = new File(SYSTEM_TEMP_FOLDER, "gatling-results");
    private static final File GATLING_RESULTS_FOLDER = new File("results");

    /**
     * Get the path to the Gatling results folder used during runtime.
     * This is where Gatling will store the results of the test runs.
     * 
     * @return The path
     */
    public static String getGatlingResultsFolder() {
        return GATLING_RESULTS_FOLDER.getAbsolutePath();
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createGatlingResultsFolder();
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String resourceLocation = "file://" + getGatlingResultsFolder();
        logger.info("Ajout du gestionnaire de ressources pour: " + resourceLocation);

        registry.addResourceHandler("/api/performance/gatling/results/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new EncodedResourceResolver());
    }

    /**
     * Create the folder that Gatling requires at runtime.
     */
    private void createGatlingResultsFolder() {
        if (!GATLING_RESULTS_FOLDER.exists()) {
            boolean success = GATLING_RESULTS_FOLDER.mkdirs();
            if (success) {
                logger.info("Gatling results folder created successfully at: " + GATLING_RESULTS_FOLDER.getAbsolutePath());
            } else {
                logger.error("Failed to create Gatling results folder at: " + GATLING_RESULTS_FOLDER.getAbsolutePath());
                throw new RuntimeException("Failed to create Gatling results folder!");
            }
        } else {
            logger.info("Gatling results folder already exists at: " + GATLING_RESULTS_FOLDER.getAbsolutePath());
        }
    }
}
