package com.commercehub.dropwizard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by awilson on 11/18/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayDeadConfiguration extends Configuration {

    @NotBlank
    private String stateFilePath;
    private String contextPath = "/ready";

    public String getStateFilePath() {
        return stateFilePath;
    }

    public void setStateFilePath(String stateFilePath) {
        this.stateFilePath = stateFilePath;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
