package ca.etsmtl.taf.performance.jmeter;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.exceptions.CsvException;

import ca.etsmtl.taf.performance.jmeter.model.FTPTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.HttpTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.JMeterResponse;
import ca.etsmtl.taf.performance.jmeter.model.TestPlanBase;
import ca.etsmtl.taf.performance.jmeter.utils.JMeterRunner;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/performance/jmeter")
public class JmeterController {

  private ResponseEntity<JMeterResponse> executeTestPlan(TestPlanBase testPlan, String type) {

    // List<Map<String, String>> result = null;
    JMeterResponse jMeterResponse = new JMeterResponse("", "", null, null);

    try {
      // result = JMeterRunner.executeTestPlan(type);
      jMeterResponse = JMeterRunner.executeTestPlanAndGenerateReport(testPlan);
      jMeterResponse.setStatus("success");
      jMeterResponse.setMessage("Test plan executed successfully");
      return ResponseEntity.ok().body(jMeterResponse);
    } catch (JMeterRunnerException e) {
      jMeterResponse.setStatus("failure");
      jMeterResponse.setMessage(e.getMessage());
      return ResponseEntity.badRequest().body(jMeterResponse);
    } catch (RuntimeException e) {
      jMeterResponse.setStatus("failure");
      jMeterResponse.setMessage(e.getMessage());
      return ResponseEntity.internalServerError().body(jMeterResponse);
    }
  }

  @PostMapping("/http")
  public ResponseEntity<?> getHttpTestPlan(@RequestBody HttpTestPlan jmeterTestPlan)
      throws IOException, CsvException {
    if (jmeterTestPlan.getProtocol() == null) {
      jmeterTestPlan.setProtocol("http");
    }
    if (jmeterTestPlan.getPort() == null) {
      jmeterTestPlan.setPort("");
      
    }
    if (jmeterTestPlan.getDuration() == null) {
      jmeterTestPlan.setDuration("");
    }
    if (jmeterTestPlan.getData() == null) {
      jmeterTestPlan.setData("");
    }
    return executeTestPlan(jmeterTestPlan, "http");
  }

  @PostMapping("/ftp")
  public ResponseEntity<?> getFtpTestplan(@RequestBody FTPTestPlan ftpTestPlan) throws IOException, CsvException {
    return executeTestPlan(ftpTestPlan, "ftp");
  }
}