package ca.etsmtl.taf.performance.jmeter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/api/performance/jmeter")
public class JmeterController {

  private ResponseEntity<?> executeTestPlan(TestPlanBase testPlan, String type) {

    testPlan.generateTestPlan();

    List<Map<String, String>> result = null;
    try {
      result = JMeterRunner.executeTestPlan(type);
    } catch (JMeterRunnerException e) {
      return ResponseEntity.badRequest().body("Error while running JMeter test plan: " + e.getMessage());
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/http")
  public ResponseEntity<?> getHttpTestPlan(@RequestBody HttpTestPlan jmeterTestPlan)
      throws IOException, CsvException {
    return executeTestPlan(jmeterTestPlan, "http");
  }

  @PostMapping("/ftp")
  public ResponseEntity<?> getFtpTestplan(@RequestBody FTPTestPlan ftpTestPlan) throws IOException, CsvException {
    return executeTestPlan(ftpTestPlan, "ftp");
  }
}