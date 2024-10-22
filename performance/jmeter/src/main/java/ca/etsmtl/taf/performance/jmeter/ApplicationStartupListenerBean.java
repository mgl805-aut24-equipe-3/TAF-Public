package ca.etsmtl.taf.performance.jmeter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import ca.etsmtl.taf.performance.jmeter.utils.JMeterRunner;

@Component
public class ApplicationStartupListenerBean {

    private static final Logger logger = LoggerFactory.getLogger(JMeterRunner.class);

    public static final String JMETER_TEMP_FOLDER = System.getProperty("java.io.tmpdir")
            + System.getProperty("file.separator") + "jmeter"
            + System.getProperty("file.separator");

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        File tempJmeterFolder = new File(JMETER_TEMP_FOLDER);
        if (!tempJmeterFolder.exists()) {
            boolean success = tempJmeterFolder.mkdirs();

            if (success) {
                logger.info("Directory created successfully!");
            } else {
                logger.info("Failed to create directory!");
                // Dramatic failure
                throw new RuntimeException();
            }
        }

        // Read JMX files in resources and copy them to the temp folder
        // TODO: Use nio.Files.copy() instead of reading and writing byte by byte
        FileInputStream httpTestPlanSrc = null;
        FileOutputStream httpTestPlanDest = null;
        FileInputStream ftpTestPlanSrc = null;
        FileOutputStream ftpTestPlanDest = null;
        try {
            httpTestPlanSrc = new FileInputStream(
                    new ClassPathResource("jmeter/HttpSamplerTemplate.jmx").getFile());
            httpTestPlanDest = new FileOutputStream(new File(JMETER_TEMP_FOLDER + "HttpSamplerTemplate.jmx"));
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = httpTestPlanSrc.read(buf)) > 0) {
                httpTestPlanDest.write(buf, 0, bytesRead);
            }
            ftpTestPlanSrc = new FileInputStream(
                    new ClassPathResource("jmeter/FTPSamplerTemplate.jmx").getFile());
            ftpTestPlanDest = new FileOutputStream(new File(JMETER_TEMP_FOLDER + "FTPSamplerTemplate.jmx"));
            while ((bytesRead = ftpTestPlanSrc.read(buf)) > 0) {
                ftpTestPlanDest.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (httpTestPlanSrc != null)
                    httpTestPlanSrc.close();
                if (httpTestPlanDest != null)
                    httpTestPlanDest.close();
                if (ftpTestPlanSrc != null)
                    ftpTestPlanSrc.close();
                if (ftpTestPlanDest != null)
                    ftpTestPlanDest.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
