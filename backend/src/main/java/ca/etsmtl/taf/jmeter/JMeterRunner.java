package ca.etsmtl.taf.jmeter;

import ca.etsmtl.taf.jmeter.provider.JmeterPathProvider;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JMeterRunner {

  private static final Logger logger = LoggerFactory.getLogger(JMeterRunner.class);

  public static String runJMeter(String testType) throws URISyntaxException {

    String jmxFilePath = ApplicationStartupListenerBean.JMETER_TEMP_FOLDER + "TestPlan.jmx";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());

    String resultsFilePath = new StringBuilder().append(ApplicationStartupListenerBean.JMETER_TEMP_FOLDER)
        .append("results")
        .append(System.getProperty("file.separator")).append(timestamp).append(".csv").toString();

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
    }
  }

  public static List<Map<String, String>> convertCSVtoJSON(String csvFilePath)
      throws IOException, CsvException, CsvException {
    try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
      List<String[]> csvData = reader.readAll();

      String[] headers = csvData.get(0);

      return csvData.stream()
          .skip(1) // Skip the header row
          .map(row -> {
            Map<String, String> jsonMap = createJsonMap(headers, row);
            return jsonMap;
          })
          .collect(Collectors.toList());
    }
  }

  private static Map<String, String> createJsonMap(String[] headers, String[] values) {
    return IntStream.range(0, headers.length)
        .boxed()
        .collect(Collectors.toMap(i -> headers[i], i -> values[i]));
  }

}
