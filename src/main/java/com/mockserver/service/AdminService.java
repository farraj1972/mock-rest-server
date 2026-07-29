package com.mockserver.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.mockserver.model.EndpointDefinition;
import com.mockserver.model.EndpointForward;

@Service
public class AdminService {

    private final EndpointRegistry endpointRegistry;

    public AdminService(EndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    public Collection<EndpointDefinition> getEndpoints() {
        return endpointRegistry.getEndpoints();
    }

    public EndpointDefinition getEndpoint(String id) {
        return endpointRegistry.get(id, true);
    }

    public void updateForward(String id,
                              EndpointForward forward) {
        endpointRegistry.updateForward(id, forward);
    }

    public void enableForward(String id) {
        endpointRegistry.enableForward(id);
    }

    public void disableForward(String id) {
        endpointRegistry.disableForward(id);
    }

}