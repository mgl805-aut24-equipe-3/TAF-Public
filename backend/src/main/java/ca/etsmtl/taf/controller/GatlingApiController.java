package ca.etsmtl.taf.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.etsmtl.taf.entity.GatlingRequest;
import ca.etsmtl.taf.payload.response.MessageResponse;
import ca.etsmtl.taf.provider.GatlingJarPathProvider;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/gatling")
public class GatlingApiController {
    /**
     * @param gatlingRequest
     * @return
     */
    @PostMapping(value = "/runSimulation")
    public ResponseEntity<MessageResponse> runSimulation(@RequestBody GatlingRequest gatlingRequest) {
        try {
            String gatlingJarPath = new GatlingJarPathProvider().getGatlingJarPath();
            String testRequest = "{\"baseUrl\":\"" + gatlingRequest.getTestBaseUrl()
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
                return new ResponseEntity<>(new MessageResponse(output.toString()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new MessageResponse(output.toString()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
