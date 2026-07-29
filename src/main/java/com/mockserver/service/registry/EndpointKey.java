package com.mockserver.service.registry;

public record EndpointKey(
        String method,
        String path) {
}