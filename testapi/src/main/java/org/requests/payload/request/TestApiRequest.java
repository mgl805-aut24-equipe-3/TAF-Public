package org.requests.payload.request;

import com.fasterxml.jackson.databind.JsonNode;
import org.requests.Method;

import javax.validation.constraints.NotBlank;
import java.util.Map;

public class TestApiRequest {
    private Method method;

    private String authorization;

    private String bearerToken;

    @NotBlank
    private String apiUrl;

    private int statusCode;

    private String input;

    private JsonNode expectedOutput;

    private int responseTime;

    private Map<String, String> expectedHeaders;

    private Map<String, String> headers;

    public Method getMethod() { return this.method; }
    public void setMethod(String method) { this.method = Method.valueOf(method.toUpperCase()); }

    public String getAuthorization() { return this.authorization; }
    public void setAuthorization(String authorization) { this.authorization = authorization; }

    public String getBearerToken() { return this.bearerToken; }
    public void setBearerToken(String bearerToken) { this.bearerToken = bearerToken; }

    public String getApiUrl() { return this.apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public int getStatusCode(){ return this.statusCode; }

    public String getInput() { return this.input; }
    public void setInput(String input) { this.input = input; }

    public JsonNode getExpectedOutput() {
        return this.expectedOutput;
    }

    public void setExpectedOutput(JsonNode expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public int getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public Map<String, String> getExpectedHeaders() {
        return this.expectedHeaders;
    }

    public void setExpectedHeaders(Map<String, String> expectedHeaders) {
        this.expectedHeaders = expectedHeaders;
    }

    public Map<String, String> getHeaders() {
        if ("bearer_token".equals(getAuthorization())) {
            this.headers.put("Authorization", getBearerToken());
        }
        System.out.println(" ---------------- finalll Headers"+ this.headers.toString());
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}


