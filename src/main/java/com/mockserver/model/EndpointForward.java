package com.mockserver.model;

public class EndpointForward {

    private boolean enabled = false;

    private String url;

    public boolean isEnabled() {
     return enabled;
    }

    public void setEnabled(boolean enabled) {
     this.enabled = enabled;
    }

    public String getUrl() {
     return url;
    }

    public void setUrl(String url) {
     this.url = url;
    }

    // getters/setters
}