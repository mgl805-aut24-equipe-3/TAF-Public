package ca.etsmtl.taf.performance.jmeter;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.exceptions.CsvException;

import ca.etsmtl.taf.performance.jmeter.model.FTPTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.HttpTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.TestPlanBase;
import ca.etsmtl.taf.performance.jmeter.utils.JMeterRunner;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/jmeter")
public class JmeterController {

  private ResponseEntity<?> executeTestPlan(TestPlanBase testPlan, String type) {
    testPlan.generateTestPlan();
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Submit the task to the ExecutorService
    AtomicReference<String> resultPathRef = new AtomicReference<>();
    Future<?> future = executorService.submit(() -> {
      String result = null;
      try {
        result = JMeterRunner.runJMeter(type);
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
      resultPathRef.set(result);
    });
    try {
      // Wait for the task to finish
      future.get();
    } catch (ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during task execution.");
    } catch (InterruptedException e){
      Thread.currentThread().interrupt();
      return null;
    }
    finally {
      // Shutdown the ExecutorService to release resources
      executorService.shutdown();
    }
    List<Map<String, String>> result = null;
    String resultPath = resultPathRef.get();
    if (resultPath != null && !resultPath.isEmpty()) {
      try {
        result = JMeterRunner.convertCSVtoJSON(resultPath);
      } catch (IOException | CsvException e) {
        throw new RuntimeException(e);
      }
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/http")
  public ResponseEntity<?> getJmeterTestPlan(@RequestBody HttpTestPlan jmeterTestPlan) throws IOException, CsvException {
    return executeTestPlan(jmeterTestPlan, "http");
  }

  @PostMapping("/ftp")
  public ResponseEntity<?> getFtpTestplan(@RequestBody FTPTestPlan ftpTestPlan) throws IOException, CsvException {
    return executeTestPlan(ftpTestPlan, "ftp");
  }
}