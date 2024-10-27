package ca.etsmtl.taf.performance.jmeter.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    private static final List<String> JMETER_PROPERTIES_FILES = List.of("jmeter.properties", "log4j2.xml",
            "reportgenerator.properties", "saveservice.properties", "system.properties", "upgrade.properties",
            "user.properties");

    private static final List<String> JMETER_TEMPLATES_FILES = List.of("FTPSamplerTemplate.jmx",
            "HTTPSamplerTemplate.jmx");

    /**
     * Get the path to the JMeter home folder used during runtime
     * 
     * @return The path
     */
    public static String getJmeterHome() {
        return JMETER_TEMP_FOLDER.getAbsolutePath();
    }

    /**
     * Get the path to the JMeter bin folder used during runtime.
     * This is where all JMeter properties file are stored.
     * 
     * @return The path
     */
    public static String getJmeterBinFolder() {
        return JMETER_BIN_FOLDER.getAbsolutePath();
    }

    /**
     * Get the path to the JMeter templates folder used during runtime.
     * The templates most contains JMX files (test plans) that can be used as either
     * HTTP or FTP tests.
     * 
     * @return The path
     */
    public static String getJmeterTemplatesFolder() {
        return JMETER_TEMPLATES_FOLDER.getAbsolutePath();
    }

    /**
     * Get the path to the JMeter results folder used during runtime.
     * This is where JMeter will store the results of the test runs.
     * 
     * @return The path
     */
    public static String getJmeterResultsFolder() {
        return JMETER_RESULTS_FOLDER.getAbsolutePath();
    }

    /**
     * Get the absolute path to the JMX file containing the test plan for HTTP
     * tests.
     * 
     * @return The absolute path path
     */
    public static String getHTTPSamplerTemplate() {
        return new File(JMETER_TEMPLATES_FOLDER, "HTTPSamplerTemplate.jmx").getAbsolutePath();
    }

    /**
     * Get the absolute path to the JMX file containing the test plan for FTP
     * tests.
     * 
     * @return The absolute path path
     */
    public static String getFTPSamplerTemplate() {
        return new File(JMETER_TEMPLATES_FOLDER, "FTPSamplerTemplate.jmx").getAbsolutePath();
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
     * Create the temp folder for JMeter and the templates folder used at runtime.
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
     * JMeter properties files are found in the Maven package
     * org.apache.jmeter:ApacheJMeter_config
     * Using {@link java.lang.ClassLoader#getResourceAsStream(String)
     * getResourceAsStream} to ensure portability.
     * 
     * @see <a href=
     *      "https://www.baeldung.com/java-classpath-resource-cannot-be-opened">How
     *      to Avoid the Java FileNotFoundException When Loading Resources</a>
     */
    private void copyJMeterPropertiesFiles() {
        for (String propertiesFile : JMETER_PROPERTIES_FILES) {
            try (var inputStream = getClass().getResourceAsStream("/bin/" + propertiesFile)) {
                if (inputStream == null) {
                    logger.warn("Resource not found: {}", propertiesFile);
                    continue;
                }
                Path destinationPath = new File(JMETER_BIN_FOLDER, propertiesFile).toPath();
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Copied JMeter properties file: {} -> {}", propertiesFile, destinationPath);
            } catch (IOException e) {
                logger.error("Error copying JMeter properties file: {}", propertiesFile, e);
            }
        }
    }

    /**
     * Copy all JMX template files from resources to the temp/templates folder
     * Using {@link java.lang.ClassLoader#getResourceAsStream(String)
     * getResourceAsStream} to ensure portability.
     * 
     * @see <a href=
     *      "https://www.baeldung.com/java-classpath-resource-cannot-be-opened">How
     *      to Avoid the Java FileNotFoundException When Loading Resources</a>
     */
    private void copyJMXTemplateFiles() {
        for (String templateFile : JMETER_TEMPLATES_FILES) {
            try (var inputStream = getClass().getResourceAsStream("/org/apache/jmeter/templates/" + templateFile)) {
                if (inputStream == null) {
                    logger.warn("Resource not found: {}", templateFile);
                    continue;
                }
                Path destinationPath = new File(JMETER_TEMPLATES_FOLDER, templateFile).toPath();
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Copied JMeter template file: {} -> {}", templateFile, destinationPath);
            } catch (IOException e) {
                logger.error("Error copying JMeter template file: {}", templateFile, e);
            }
        }

    }

}
