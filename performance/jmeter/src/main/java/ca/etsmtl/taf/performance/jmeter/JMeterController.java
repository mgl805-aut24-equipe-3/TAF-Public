package ca.etsmtl.taf.performance.jmeter;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.etsmtl.taf.performance.jmeter.config.JMeterConfigurator;
import ca.etsmtl.taf.performance.jmeter.model.FTPTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.HttpTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.JMeterResponse;
import ca.etsmtl.taf.performance.jmeter.model.TestPlanBase;
import ca.etsmtl.taf.performance.jmeter.utils.JMeterRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/performance/jmeter")
public class JMeterController {

  private static final Logger logger = LoggerFactory.getLogger(JMeterController.class);

  private ResponseEntity<JMeterResponse> executeTestPlan(TestPlanBase testPlan) {

    JMeterResponse jMeterResponse = new JMeterResponse("", "", null, null);

    try {
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
  public ResponseEntity<JMeterResponse> getHttpTestPlan(@RequestBody HttpTestPlan jmeterTestPlan) {
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
    return executeTestPlan(jmeterTestPlan);
  }

  @PostMapping("/ftp")
  public ResponseEntity<JMeterResponse> getFtpTestplan(@RequestBody FTPTestPlan ftpTestPlan) {
    return executeTestPlan(ftpTestPlan);
  }

  @GetMapping("/latest-report")
  public ResponseEntity<String> getLatestReportUrl() {
    String latestReportUrl = JMeterConfigurator.getLatestReportUrl();
    if (latestReportUrl != null) {
        logger.info("URL du dernier rapport JMeter : {}", latestReportUrl);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(latestReportUrl)).build();
    }
    logger.info("Aucun rapport trouvé.");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucun rapport trouvé.");
  }
}
