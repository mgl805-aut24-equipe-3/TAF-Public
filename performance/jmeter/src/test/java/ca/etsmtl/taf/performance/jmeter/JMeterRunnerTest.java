package ca.etsmtl.taf.performance.jmeter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.etsmtl.taf.performance.jmeter.config.JMeterConfigurator;
import ca.etsmtl.taf.performance.jmeter.model.HttpTestPlan;
import ca.etsmtl.taf.performance.jmeter.model.JMeterResponse;
import ca.etsmtl.taf.performance.jmeter.model.TestPlanBase;
import ca.etsmtl.taf.performance.jmeter.utils.JMeterRunner;

public class JMeterRunnerTest {
    @BeforeAll
    static void setUp() {
        // Need to initialize the JMeter and copy the JMX files to the temp folder
        JMeterConfigurator jMeterConfigurator = new JMeterConfigurator();
        jMeterConfigurator.onApplicationEvent(null);
    }

    @Test
    void testExecuteHttpTestPlan() {

        // "nbThreads": "3",
        // "rampTime": "5",
        // "duration": "",
        // "domain": "httpbin.org",
        // "port": "",
        // "protocol": "https",
        // "path": "/get",
        // "method": "GET",
        // "loop": "2"

        HttpTestPlan testPlan = new HttpTestPlan();
        testPlan.setNbThreads("3");
        testPlan.setRampTime("5");
        testPlan.setProtocol("https");
        testPlan.setDomain("httpbin.org");
        testPlan.setMethod("GET");
        testPlan.setPath("/get");
        testPlan.setLoop("2");
        testPlan.setDuration("");
        testPlan.setPort("");
        testPlan.setData("");

        try {
            JMeterResponse response = JMeterRunner.executeTestPlanAndGenerateReport((TestPlanBase) testPlan);

            assertTrue(response.getDetails().getContentType().equalsIgnoreCase("html"), "Content type should be 'html'"); 
            assertTrue(response.getSummary() != null, "Summary should not be empty");           
            assertTrue(!response.getDetails().getLocationURL().isEmpty(), "Location URL should not be empty");

        } catch (JMeterRunnerException e) {
            fail();
        }
    }
}
