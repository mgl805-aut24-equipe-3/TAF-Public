package ca.etsmtl.taf.performance.jmeter.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import ca.etsmtl.taf.performance.jmeter.JMeterRunnerException;
import ca.etsmtl.taf.performance.jmeter.config.JMeterConfigurator;
import ca.etsmtl.taf.performance.jmeter.model.TestPlanBase;
import ca.etsmtl.taf.performance.jmeter.provider.JmeterPathProvider;

/**
 * This class is responsible for running JMeter tests and converting the results
 * to JSON.
 * 
 * @version 1.0
 */
public class JMeterRunner {

  private static final Logger logger = LoggerFactory.getLogger(JMeterRunner.class);

  /**
   * Run the JMeter test plan and return the path to the results file.
   * Requires JMeter to be installed on the local system and the environment
   * variable JMETER_INSTALL_DIR to be set.
   * 
   * @deprecated User {@link #executeTestPlan(String)} instead
   * @param testType The type of test to run (e.g. http, ftp)
   * @return The path to the results file
   * @throws URISyntaxException
   */
  public static String runJMeter(String testType) throws URISyntaxException {

    String jmxFilePath = new File(JMeterConfigurator.getJmeterTemplatesFolder(), "TestPlan.jmx").getAbsolutePath();

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());

    String resultsFilePath = new StringBuilder().append(JMeterConfigurator.getJmeterHome())
        .append("results").append(System.getProperty("file.separator")).append(timestamp).append(".csv").toString();

    String jmeterExecutable = new JmeterPathProvider().getJmeterJarPath();
    try {
      String jmeterCommand = jmeterExecutable + " -n -t " + jmxFilePath + " -l " + resultsFilePath;
      // Run the command
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec(jmeterCommand);
      int exitCode = process.waitFor();

      // Check the exit code
      if (exitCode == 0) {
        return resultsFilePath;
      } else {
        return null;
      }

    } catch (IOException e) {
      return null;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Run the JMeter test plan and return the results as a list of maps.
   * 
   * @param type The type of test to run (e.g. http, ftp)
   * @return The results as a list of maps
   * @throws JMeterRunnerException
   */
  public static List<Map<String, String>> executeTestPlan(String type) throws JMeterRunnerException {

    List<Map<String, String>> results = null;
    File resultsFile = getResultsFile();
    try {
      initializeJMeter();

      runTests(resultsFile);

      results = JMeterRunner.convertCSVtoJSON(resultsFile.getAbsolutePath());

    } catch (IOException | CsvException | JMeterRunnerException e) {
      throw new JMeterRunnerException(e.getMessage(), e);
    }

    return results;

  }

  public static String executeTestPlanAndGenerateReport(TestPlanBase testPlan) throws JMeterRunnerException {

    String reportLocation = null;
    File resultsFile = getResultsFile();
    try {
      testPlan.generateTestPlan();

      initializeJMeter();

      reportLocation = runTests(resultsFile);

    } catch (JMeterRunnerException e) {
      throw new JMeterRunnerException(e.getMessage(), e);
    }

    return reportLocation;

  }

  private static final File getTestPlan() throws JMeterRunnerException {
    File jmxFilePath = new File(JMeterConfigurator.getJmeterTemplatesFolder(), "TestPlan.jmx");
    logger.info("Using Test Plan {}", jmxFilePath);

    return jmxFilePath;
  }

  private static final File getResultsFile() {

    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    File resultsFile = new File(JMeterConfigurator.getJmeterResultsFolder(), timestamp + ".csv");
    logger.info("Results will be logged in {}", resultsFile.toPath());

    return resultsFile;
  }

  private static void initializeJMeter() throws JMeterRunnerException {

    JMeterUtils
        .loadJMeterProperties(new File(JMeterConfigurator.getJmeterBinFolder(), "jmeter.properties").getAbsolutePath());

    JMeterUtils.initLocale();

    JMeterUtils.setJMeterHome(JMeterConfigurator.getJmeterHome());

    try {
      SaveService.loadProperties();

    } catch (IOException e) {
      throw new JMeterRunnerException(e.getMessage(), e);
    }

  }

  private static String runTests(File resultsFile) throws JMeterRunnerException {

    try {
      // Used to wait for the tests to finish
      CountDownLatch latch = new CountDownLatch(1);

      HashTree tree = SaveService.loadTree(getTestPlan());

      JMeterTreeModel treeModel = new JMeterTreeModel();
      JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
      treeModel.addSubTree(tree, root);

      Summariser summeriser = null;
      String summariserName = JMeterUtils.getPropDefault("summariser.name", "");
      if (summariserName.length() > 0) {
        logger.info("Creating summariser <{}>", summariserName);
        summeriser = new Summariser(summariserName);
      }

      ResultCollector collector = new ResultCollector(summeriser);
      collector.setFilename(resultsFile.getAbsolutePath());
      tree.add(tree.getArray()[0], collector);

      logger.debug("Created JMeter tree successfully using {}", getTestPlan().getName());

      JMeterEngine engine = new StandardJMeterEngine();
      tree.add(tree.getArray()[0], new TestListener(latch));

      engine.configure(tree);

      engine.runTest();

      // Wait for the tests to finish
      latch.await();

      // Generate HTML dashboard report
      String htmlOutputDir = new File(JMeterConfigurator.getJmeterResultsFolder(),
          "dashboard_" + getResultsFile().getName().replace(".csv", "")).getAbsolutePath();
      String jsonOutputDir = new File(JMeterConfigurator.getJmeterResultsFolder(),
          "json_" + getResultsFile().getName().replace(".csv", "")).getAbsolutePath();
      JMeterUtils.setProperty("jmeter.reportgenerator.exporter.html.property.output_dir", htmlOutputDir);
      JMeterUtils.setProperty("jmeter.reportgenerator.exporter.json.property.output_dir", jsonOutputDir);
      ReportGenerator generator = new ReportGenerator(resultsFile.getAbsolutePath(), null);
      generator.generate();

      return htmlOutputDir;

    } catch (IllegalUserActionException | IOException | InterruptedException | JMeterEngineException e) {
      logger.error("Error running JMeter test", e);
      throw new JMeterRunnerException(e.getMessage(), e);
    } catch (ConfigurationException | GenerationException e) {
      logger.error("Error generating JMeter report", e);
      throw new JMeterRunnerException(e.getMessage(), e);
    }
  }

  private static List<Map<String, String>> convertCSVtoJSON(String csvFilePath)
      throws IOException, CsvException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    ArrayNode jsonArray = mapper.createArrayNode();

    try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
      List<String[]> csvData = reader.readAll();

      String[] headers = csvData.get(0);

      for (int i = 1; i < csvData.size(); i++) {
        String[] row = csvData.get(i);
        ObjectNode jsonObject = mapper.createObjectNode();

        for (int j = 0; j < headers.length; j++) {
          jsonObject.put(headers[j], j < row.length ? row[j] : null);
        }

        jsonArray.add(jsonObject);
      }
    } catch (IndexOutOfBoundsException e) {
      // Ignore
      logger.warn("Error parsing CSV file: {}", e.getMessage());
    }

    return mapper.convertValue(jsonArray,
        new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, String>>>() {
        });
  }

  private static class TestListener implements TestStateListener {

    private CountDownLatch latch;

    public TestListener(CountDownLatch latch) {
      this.latch = latch;
    }

    @Override
    public void testStarted() {
      if (logger.isInfoEnabled()) {
        final long now = System.currentTimeMillis();
        logger.info("{} ({})", "Started JMeter tests at", now);
      }
    }

    @Override
    public void testStarted(String host) {
      logger.info("Test started on host {}", host);
    }

    @Override
    public void testEnded() {
      // Should stop waiting for the tests to finish
      latch.countDown();
      if (logger.isInfoEnabled()) {
        final long now = System.currentTimeMillis();
        logger.info("{} ({})", "Ended JMeter tests at", now);
      }
    }

    @Override
    public void testEnded(String host) {
      logger.info("Test ended on host {}", host);
    }

  }
}
