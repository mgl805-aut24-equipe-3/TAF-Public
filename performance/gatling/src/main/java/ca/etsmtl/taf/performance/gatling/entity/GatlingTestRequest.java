package ca.etsmtl.taf.performance.gatling.entity;

import com.fasterxml.jackson.annotation.JsonAlias;

public class GatlingTestRequest {
    @JsonAlias("testBaseUrl")
    private String baseUrl;
    @JsonAlias("testScenarioName")
    private String scenarioName;
    @JsonAlias("testRequestName")
    private String requestName;
    @JsonAlias("testUri")
    private String uri;
    @JsonAlias("testRequestBody")
    private String requestBody;
    @JsonAlias("testMethodType")
    private String methodType;
    @JsonAlias("testUsersNumber")
    private int usersNumber;
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getScenarioName() {
        return scenarioName;
    }
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }
    public String getRequestName() {
        return requestName;
    }
    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public String getRequestBody() {
        return requestBody;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public String getMethodType() {
        return methodType;
    }
    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
    public int getUsersNumber() {
        return usersNumber;
    }
    public void setUsersNumber(int usersNumber) {
        this.usersNumber = usersNumber;
    }

    
}