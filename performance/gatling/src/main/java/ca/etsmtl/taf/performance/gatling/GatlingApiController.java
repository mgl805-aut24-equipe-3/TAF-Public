package ca.etsmtl.taf.performance.gatling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.etsmtl.taf.performance.gatling.payload.response.MessageResponse;
import ca.etsmtl.taf.performance.gatling.entity.GatlingTestRequest;
import ca.etsmtl.taf.performance.gatling.provider.GatlingJarPathProvider;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/performance/gatling")
public class GatlingApiController {

    private static final Logger logger = LoggerFactory.getLogger(GatlingApiController.class);

    /**
     * @param gatlingRequest
     * @return
     */
    @PostMapping(value = "/runSimulation")
    public ResponseEntity<MessageResponse> runSimulation(@RequestBody GatlingTestRequest gatlingRequest) {
        try {

            // Convert GatlingRequest to JSON String
            ObjectMapper objectMapper = new ObjectMapper();
            String testRequest = objectMapper.writeValueAsString(gatlingRequest);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            PipedOutputStream pipedOut = new PipedOutputStream();
            PipedInputStream pipedIn = new PipedInputStream(pipedOut);
            BufferedReader reader = new BufferedReader(new InputStreamReader(pipedIn));

            Future<?> future = executor.submit(() -> {
                try {
                    @SuppressWarnings("java:S106")
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(pipedOut));
                    try {
                        logger.info("Executing Gatling with request: {}", testRequest);
                        Main.main(new String[] { testRequest });
                    } finally {
                        System.setOut(originalOut);
                        pipedOut.close();
                    }
                } catch (Exception e) {
                    logger.error("Error happened executing Gatling", e);
                }
            });

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            reader.close();

            return runGatling(future, output.toString());

        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<MessageResponse> runGatling(Future<?> future, String output) {
        try {
            future.get();
            return new ResponseEntity<>(new MessageResponse(parseOutput(output)),
                    HttpStatus.OK);
        } catch (InterruptedException | ExecutionException e) {
            // Dramatic failure
            Thread.currentThread().interrupt();
            return new ResponseEntity<>(new MessageResponse(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String parseOutput(String output) {
        StringBuilder returnString = new StringBuilder(
                "---- Global Information --------------------------------------------------------\n");

        String startPattern = "---- Global Information --------------------------------------------------------";
        String endPattern = "---- Response Time Distribution ------------------------------------------------";
        Pattern pattern = Pattern.compile(startPattern + "(.*?)" + endPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            returnString.append(matcher.group(1).trim()).append("\n");
        } else {
            returnString.append("Not found in Gatling output.");
        }

        returnString.append("---- Generated Report ------------------------------------------------------\n");

        String regex = "Please open the following file: (file:///[^\\s]+|https?://[^\\s]+)";

        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        matcher = pattern.matcher(output);

        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                returnString.append(matcher.group(i).trim());
            }
        } else {
            returnString.append("Not found in Gatling output.");
        }

        return returnString.toString();
    }

    @GetMapping("/latest-report")
    public ResponseEntity<String> getLatestGatlingReport() {
        try {
            Path reportsDir = Paths.get(GatlingConfigurator.getGatlingResultsFolder()).toAbsolutePath().normalize();
            File reportsDirFile = reportsDir.toFile();

            if (!reportsDirFile.exists() || !reportsDirFile.isDirectory()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Le répertoire des résultats Gatling n'existe pas.");
            }

            // Trouver le sous-répertoire le plus récent
            File latestReportDir = Arrays.stream(reportsDirFile.listFiles(File::isDirectory))
                    .max(Comparator.comparingLong(File::lastModified))
                    .orElse(null);

            if (latestReportDir != null) {
                File reportFile = new File(latestReportDir, "index.html");

                if (reportFile.exists()) {

                    // Redirect to the latest report available at the path
                    // /reports/performance/gatling/dashboard/
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(
                            URI.create("/reports/performance/gatling/dashboard/" + latestReportDir.getName() + "/index.html"));
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);

                    
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Aucun rapport trouvé dans le dernier répertoire.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun répertoire de rapport trouvé.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur de lecture du fichier de rapport.");
        }
    }

}