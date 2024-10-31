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
    private static final Logger logger = LoggerFactory.getLogger(GatlingConfigurator.class); //Pour les logs

    //A decommenter si on veut utiliser le dossier temporaire du système et aussi pour gatling.resultsFolder du fichier gatling.conf, comment s'y prendre pour le coter temporaire doit-on juste le retirer ???? 

    //private static final File SYSTEM_TEMP_FOLDER = new File(System.getProperty("java.io.tmpdir"));
    //private static final File GATLING_RESULTS_FOLDER = new File(SYSTEM_TEMP_FOLDER, "gatling-results");

    private static final File GATLING_RESULTS_FOLDER = new File(System.getProperty("user.dir"), "results"); //Pour respecter le chemin de la propriété gatling.resultsFolder 
    //private static final File GATLING_RESULTS_FOLDER = new File("results");

    public static String getGatlingResultsFolder() {
        return GATLING_RESULTS_FOLDER.getAbsolutePath();
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        createGatlingResultsFolder();
        //logFolderContent();
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        String resourceLocation = "file:" + getGatlingResultsFolder() + "/";
        logger.info("Configuration du gestionnaire de ressources pour: {}", resourceLocation); //Pour les logs
        
        registry.addResourceHandler("/api/performance/gatling/results/**")
               .addResourceLocations(resourceLocation)
               .setCachePeriod(3600)  // Cache les ressources pendant 1 heure
               .resourceChain(true)  // Active la chaîne de ressources
               .addResolver(new EncodedResourceResolver());
    }

    private void createGatlingResultsFolder() {
        if (!GATLING_RESULTS_FOLDER.exists()) {
            boolean success = GATLING_RESULTS_FOLDER.mkdirs();
            if (success) {
                logger.info("Dossier de résultats Gatling créé avec succès : {}", //Pour les logs
                    GATLING_RESULTS_FOLDER.getAbsolutePath());
            } else {
                logger.error("Impossible de créer le dossier de résultats Gatling : {}", //Pour les logs
                    GATLING_RESULTS_FOLDER.getAbsolutePath());
                throw new RuntimeException("Échec de la création du dossier de résultats Gatling!");
            }
        } else {
            logger.info("Le dossier de résultats Gatling existe déjà : {}", //Pour les logs
                GATLING_RESULTS_FOLDER.getAbsolutePath());
        }
    }

    // Aide pour le débogage
    // private void logFolderContent() {
    //     File[] files = GATLING_RESULTS_FOLDER.listFiles();
    //     if (files != null) {
    //         logger.info("Contenu du dossier de résultats Gatling :");
    //         for (File file : files) {
    //             logger.info(" - {} ({})", file.getName(), 
    //                 file.isDirectory() ? "dossier" : "fichier");
    //             if (file.isDirectory()) {
    //                 File[] subFiles = file.listFiles();
    //                 if (subFiles != null) {
    //                     for (File subFile : subFiles) {
    //                         logger.info("   └── {}", subFile.getName());
    //                     }
    //                 }
    //             }
    //         }
    //     } else {
    //         logger.warn("Impossible de lister le contenu du dossier de résultats Gatling");
    //     }
    // }
}
