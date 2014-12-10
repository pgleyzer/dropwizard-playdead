package com.commercehub.dropwizard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotBlank;

import java.nio.file.Path;
import java.nio.file.Paths;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayDeadConfiguration extends Configuration {

    public static final String ADMIN_ENVIRONMENT = "admin";
    public static final String APPLICATION_ENVIRONMENT = "application";

    @NotBlank
    private Path stateFilePath;
    private String accessKey;
    private String contextPath = "/ready";
    private String environment = APPLICATION_ENVIRONMENT;
    private boolean showMessageOnError = false;

    public Path getStateFilePath() {
        return stateFilePath;
    }

    public void setStateFilePath(String stateFilePath) {
        this.stateFilePath = Paths.get(stateFilePath);
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public boolean showMessageOnError() {
        return showMessageOnError;
    }

    public void setShowMessageOnError(boolean showMessageOnError) {
        this.showMessageOnError = showMessageOnError;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
}
