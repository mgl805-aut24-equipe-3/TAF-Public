package ca.etsmtl.taf.performance.jmeter.model;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ca.etsmtl.taf.performance.jmeter.config.JMeterConfigurator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JMeterResponse {

    private String status;
    @JsonProperty("status-message")
    private String message;
    private Map<String, Object> summary;
    private JMeterResponseDetails details;

    public JMeterResponse(String status, String message, Map<String, Object> summary, JMeterResponseDetails details) {
        this.status = status;
        this.message = message;
        this.summary = summary;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    public JMeterResponseDetails getDetails() {
        return details;
    }

    public void setDetails(JMeterResponseDetails details) {
        this.details = details;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class JMeterResponseDetails {
        @JsonProperty("content-type")
        private String contentType;

        @JsonProperty("location-url")
        private String locationURL;

        public JMeterResponseDetails(String contentType, String url) {
            this.contentType = contentType;
            this.locationURL = url;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getLocationURL() {
            return locationURL;
        }

        public void setLocationURL(String url) {

            Path dashboardPath = new File(JMeterConfigurator.getJmeterResultsFolder()).toPath()
                    .relativize(new File(url).toPath());
            this.locationURL = "/api/performance/jmeter/dashboard/" + dashboardPath.toString() + "/index.html";
        }

    }
}
