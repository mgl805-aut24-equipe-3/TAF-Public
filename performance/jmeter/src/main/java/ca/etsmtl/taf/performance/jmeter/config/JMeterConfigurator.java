package ca.etsmtl.taf.performance.jmeter.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.jmeter.gui.action.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for performing initialization tasks on application
 * startup:
 * - Creating a temp folder for JMeter
 * - Copying the JMX template files from resources to the temp folder
 * - Copying JMeter properties files from resources to the temp folder
 * 
 * These files are necessary for running JMeter test plans.
 * 
 * @version 1.0
 */
@Component
public class JMeterConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(JMeterConfigurator.class);
    private static final File SYSTEM_TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));

    private static final File JMETER_TEMP_FOLDER = new File(SYSTEM_TEMP_FOLDER, "jmeter");
    private static final File JMETER_BIN_FOLDER = new File(JMETER_TEMP_FOLDER, "bin");
    private static final File JMETER_TEMPLATES_FOLDER = new File(JMETER_TEMP_FOLDER, "templates");
    private static final File JMETER_RESULTS_FOLDER = new File(JMETER_TEMP_FOLDER, "results");

    public static String getJmeterHome() {
        return JMETER_TEMP_FOLDER.getAbsolutePath();
    }

    public static String getJmeterBinFolder() {
        return JMETER_BIN_FOLDER.getAbsolutePath();
    }

    public static String getJmeterTemplatesFolder() {
        return JMETER_TEMPLATES_FOLDER.getAbsolutePath();
    }

    public static String getJmeterResultsFolder() {
        return JMETER_RESULTS_FOLDER.getAbsolutePath();
    }

    /**
     * Convenient event listener called when the SpringBootApplication has been
     * initialized and is
     * ready to listen to requests.
     * 
     * It creates a temp folder for JMeter and copies various files from embedded
     * resources found
     * in package org.apache.jmeter to the temp folder.
     * 
     * @param event ApplicationReadyEvent
     */
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {

        // Create the temp folder for JMeter and the templates folder
        createTempFolders();

        // Copy all properties files from resources folder org.apache.jmeter to the temp
        // folder
        copyJMeterPropertiesFiles();

        // Copy all JMX template files from resources folder org.apache.jmeter.templates
        // to the temp folder
        copyJMXTemplateFiles();
    }

    /**
     * Create the temp folder for JMeter and the templates folder
     */
    private void createTempFolders() {
        if (!JMETER_TEMP_FOLDER.exists()) {
            boolean success = JMETER_TEMP_FOLDER.mkdirs();

            if (success) {
                logger.info("JMeter temp folder created successfully!");
            } else {
                logger.info("Failed to create JMeter temp folder!");
                // Dramatic failure
                throw new RuntimeException();
            }
        } else {
            logger.info("JMeter temp folder already exists!");
        }

        if (!JMETER_BIN_FOLDER.exists()) {
            boolean success = JMETER_BIN_FOLDER.mkdirs();

            if (success) {
                logger.info("JMeter bin folder created successfully!");
            } else {
                logger.info("Failed to create JMeter bin folder!");
                // Dramatic failure
                throw new RuntimeException();
            }
        } else {
            logger.info("JMeter bin folder already exists!");
        }

        if (!JMETER_TEMPLATES_FOLDER.exists()) {
            boolean success = JMETER_TEMPLATES_FOLDER.mkdirs();

            if (success) {
                logger.info("JMeter templates folder created successfully!");
            } else {
                logger.info("Failed to create JMeter templates folder!");
                // Dramatic failure
                throw new RuntimeException();
            }
        } else {
            logger.info("JMeter templates folder already exists!");
        }

        // Resuls folder is created by JMeter itself
    }

    /**
     * Copy all JMeter properties files from resources to the temp folder
     */
    private void copyJMeterPropertiesFiles() {
        try {
            ClassPathResource resource = new ClassPathResource("org/apache/jmeter");
            File resourceFolder = resource.getFile();
            File[] files = resourceFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() || !file.getName().endsWith(".properties")) {
                        continue;
                    }
                    Path sourcePath = file.toPath();
                    Path destinationPath = new File(JMETER_BIN_FOLDER, file.getName()).toPath();
                    Files.copy(sourcePath, destinationPath,
                            StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Copied JMeter properties file: {} -> {}", file.getName(), destinationPath);
                }
            }
        } catch (IOException e) {
            logger.error("Error copying JMeter properties files", e);
        }
    }

    /**
     * Copy all JMX template files from resources to the temp/templates folder
     */
    private void copyJMXTemplateFiles() {
        try {
            // Create the templates folder in the temp folder
            ClassPathResource resource = new ClassPathResource("org/apache/jmeter/templates");
            File resourceFolder = resource.getFile();
            File[] files = resourceFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    Path sourcePath = file.toPath();
                    Path destinationPath = new File(
                            JMETER_TEMPLATES_FOLDER, file.getName()).toPath();
                    Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    logger.debug("Copied JMeter template file: {} -> {}", file.getName(), destinationPath);
                }
            }
        } catch (IOException e) {
            logger.error("Error copying JMX template files:", e);
        }
    }

}
