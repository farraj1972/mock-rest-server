package com.mockserver.model;

import java.util.ArrayList;
import java.util.List;

public class EndpointConfiguration {

    private List<EndpointDefinition> endpoints = new ArrayList<>();

    public EndpointConfiguration() {
    }

    public List<EndpointDefinition> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointDefinition> endpoints) {
        this.endpoints = endpoints != null
                ? endpoints
                : new ArrayList<>();
    }
}