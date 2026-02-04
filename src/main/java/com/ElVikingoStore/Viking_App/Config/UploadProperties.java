package com.ElVikingoStore.Viking_App.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    private String path;

    // Getters y setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}