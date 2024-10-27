package ca.etsmtl.taf.performance.jmeter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.etsmtl.taf.performance.jmeter.config.JMeterConfigurator;
import ca.etsmtl.taf.performance.jmeter.model.HttpTestPlan;
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

        // "nbThreads": "10",
        // "rampTime": "5",
        // "duration": "",
        // "domain": "httpbin.org",
        // "port": "",
        // "protocol": "https",
        // "path": "/get",
        // "method": "GET",
        // "loop": "3"

        HttpTestPlan testPlan = new HttpTestPlan();
        testPlan.setNbThreads("10");
        testPlan.setRampTime("5");
        testPlan.setProtocol("https");
        testPlan.setDomain("httpbin.org");
        testPlan.setMethod("GET");
        testPlan.setPath("/get");
        testPlan.setLoop("3");
        testPlan.setDuration("");
        testPlan.setPort("");
        testPlan.setData("");

        testPlan.generateTestPlan();

        try {
            List<Map<String, String>> results = JMeterRunner.executeTestPlan("http");

            assertTrue(!results.isEmpty());

        } catch (JMeterRunnerException e) {
            fail();
        }
    }
}
