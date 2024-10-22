package ca.etsmtl.taf.performance.jmeter.provider;

import java.io.File;
import java.net.URISyntaxException;

public class JmeterPathProvider {
    public String getJmeterJarPath() throws URISyntaxException {

        File jmeterExecutable = new File(System.getenv("JMETER_INSTALL_DIR") + System.getProperty("file.separator")
                + "bin" + System.getProperty("file.separator") + "jmeter.sh");

        if (jmeterExecutable.exists()) {
            return jmeterExecutable.getAbsolutePath();
        } else {
            return "JMETER JAR not found!";
        }
    }
}