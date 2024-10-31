package ca.etsmtl.taf.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Path;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.etsmtl.taf.entity.GatlingRequest;
import ca.etsmtl.taf.payload.response.MessageResponse;
import ca.etsmtl.taf.provider.GatlingJarPathProvider;
import ca.etsmtl.taf.config.GatlingConfigurator;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/gatling")
public class GatlingApiController {

    private static final Logger logger = LoggerFactory.getLogger(GatlingApiController.class);

    static boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    /**
     * @param gatlingRequest
     * @return
     */
    @PostMapping(value = "/runSimulation")
    public ResponseEntity<MessageResponse> runSimulation(@RequestBody GatlingRequest gatlingRequest) {
        try {
            String gatlingJarPath = new GatlingJarPathProvider().getGatlingJarPath();
            String testRequest = isWindows
                    ? "{\\\"baseUrl\\\":\\\"" + gatlingRequest.getTestBaseUrl() + "\\\",\\\"scenarioName\\\":\\\""
                            + gatlingRequest.getTestScenarioName() + "\\\",\\\"requestName\\\":\\\""
                            + gatlingRequest.getTestRequestName() + "\\\",\\\"uri\\\":\\\""
                            + gatlingRequest.getTestUri() + "\\\",\\\"requestBody\\\":\\\""
                            + gatlingRequest.getTestRequestBody() + "\\\",\\\"methodType\\\":\\\""
                            + gatlingRequest.getTestMethodType() + "\\\",\\\"usersNumber\\\":\\\""
                            + gatlingRequest.getTestUsersNumber() + "\\\"}"
                    :

                    "{\"baseUrl\":\"" + gatlingRequest.getTestBaseUrl()
                            + "\",\"scenarioName\":\"" + gatlingRequest.getTestScenarioName()
                            + "\",\"requestName\":\"" + gatlingRequest.getTestRequestName() + "\",\"uri\":\""
                            + gatlingRequest.getTestUri() + "\",\"requestBody\":\""
                            + gatlingRequest.getTestRequestBody() + "\",\"methodType\":\""
                            + gatlingRequest.getTestMethodType() + "\",\"usersNumber\":\""
                            + gatlingRequest.getTestUsersNumber() + "\"}";

            StringBuilder gatlingCommand = new StringBuilder();
            gatlingCommand.append("java -jar ");
            gatlingCommand.append(gatlingJarPath);
            gatlingCommand.append(" -DrequestJson=");
            gatlingCommand.append(testRequest);

            logger.info("Executing Gatling command: " + gatlingCommand.toString());

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(gatlingCommand.toString());

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("Gatling test executed successfully.");
                logger.info("Gatling output: " + output.toString());
                return new ResponseEntity<>(new MessageResponse(parseOutput(output.toString())),
                        HttpStatus.OK);
            } else {
                logger.error("Gatling test execution failed with exit code: " + exitCode);
                return new ResponseEntity<>(new MessageResponse(output.toString()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            logger.error("IOException during Gatling test execution: ", e);
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException e) {
            logger.error("URISyntaxException during Gatling test execution: ", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("InterruptedException during Gatling test execution: ", e);
            throw new RuntimeException(e);
        }
    }

    private String parseOutput(String output) {
        String returnString = "---- Global Information --------------------------------------------------------\n";

        String startPattern = "---- Global Information --------------------------------------------------------";
        String endPattern = "---- Response Time Distribution ------------------------------------------------";
        Pattern pattern = Pattern.compile(startPattern + "(.*?)" + endPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            returnString += matcher.group(1).trim() + "\n";
        } else {
            returnString += "Not found in Gatling output.";
        }

        returnString += "---- Generated Report ------------------------------------------------------\n";

        String regex = "Please open the following file: (file:///[^\\s]+|https?://[^\\s]+)";

        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        matcher = pattern.matcher(output);

        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                returnString += matcher.group(i).trim();
            }
        } else {
            returnString += "Not found in Gatling output.";
        }

        return returnString;
    }

    @GetMapping("/latest-report")
    public ResponseEntity<String> getLatestGatlingReport() {
        try {
            Path reportsDir = Paths.get(GatlingConfigurator.getGatlingResultsFolder()).toAbsolutePath().normalize();
            File reportsDirFile = reportsDir.toFile();

            if (!reportsDirFile.exists() || !reportsDirFile.isDirectory()) {
                logger.error("Le répertoire des résultats Gatling n'existe pas ou n'est pas un répertoire: " + reportsDirFile.getAbsolutePath());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Le répertoire des résultats Gatling n'existe pas.");
            }

            // Log le contenu du répertoire des résultats
            File[] files = reportsDirFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    logger.info("Contenu du répertoire des résultats: " + file.getName());
                }
            } else {
                logger.error("Impossible de lister le contenu du répertoire des résultats: " + reportsDirFile.getAbsolutePath());
            }

            // Trouver le sous-répertoire le plus récent
            File latestReportDir = Arrays.stream(reportsDirFile.listFiles(File::isDirectory))
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElse(null);

            if (latestReportDir != null) {
                File reportFile = new File(latestReportDir, "index.html");

                if (reportFile.exists()) {
                    // Lire le contenu du fichier HTML
                    StringBuilder htmlContent = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(reportFile)))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            htmlContent.append(line).append("\n");
                        }
                    }

                    // Log le contenu du fichier HTML pour vérifier
                    logger.info("Contenu du fichier HTML: " + htmlContent.toString());

                    // Modifier les chemins pour les ressources
                    String reportHtml = htmlContent.toString()
                            .replace("href=\"", "href=\"http://localhost:8083/api/performance/gatling/results/" + latestReportDir.getName() + "/")
                            .replace("src=\"", "src=\"http://localhost:8083/api/performance/gatling/results/" + latestReportDir.getName() + "/");

                    return ResponseEntity.ok()
                            .contentType(MediaType.TEXT_HTML)
                            .body(reportHtml);
                } else {
                    logger.error("Fichier index.html non trouvé dans le répertoire: " + latestReportDir.getAbsolutePath());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun rapport trouvé dans le dernier répertoire.");
                }
            } else {
                logger.error("Aucun répertoire de rapport trouvé dans: " + reportsDirFile.getAbsolutePath());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun répertoire de rapport trouvé.");
            }
        } catch (IOException e) {
            logger.error("Erreur de lecture du fichier de rapport: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de lecture du fichier de rapport.");
        }
    }

}