package com.mockserver.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockserver.model.EndpointConfiguration;
import com.mockserver.model.EndpointDefinition;
import com.mockserver.model.EndpointForward;
import com.mockserver.service.registry.EndpointKey;

import jakarta.annotation.PostConstruct;

@Service
public class EndpointRegistry {

     private static final Logger logger = LoggerFactory.getLogger(EndpointRegistry.class);

     private static final String CONFIG_FILE = "config/endpoints.json";

     private static final Set<String> SUPPORTED_METHODS = Set.of(
               "GET",
               "POST",
               "PUT",
               "PATCH",
               "DELETE");

     private final ObjectMapper objectMapper;

     private EndpointConfiguration configuration;

     private final Map<EndpointKey, EndpointDefinition> registry = new LinkedHashMap<>();
     private final Map<String, EndpointDefinition> registryById = new LinkedHashMap<>();

     public EndpointRegistry(ObjectMapper objectMapper) {
          this.objectMapper = objectMapper;
     }

     @PostConstruct
     public void load() {

          readConfiguration();

          validateConfiguration();

          buildIndex();

          logger.info(
                    "Loaded {} endpoint(s) from {}",
                    registry.size(),
                    CONFIG_FILE);

          logConfiguration();
     }

     private void logConfiguration() {

          logger.info("--------------------------------------------------");
          logger.info("Configured endpoints ({})", registry.size());

          registry.values().stream()
                    .sorted(Comparator
                              .comparing(EndpointDefinition::getMethod)
                              .thenComparing(EndpointDefinition::getPath))
                    .forEach(endpoint -> logger.info("  {}", String.format("%-6s %s",
                              endpoint.getMethod(),
                              endpoint.getPath())));

          logger.info("--------------------------------------------------");
     }

     private void readConfiguration() {

          try (InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream(CONFIG_FILE)) {

               if (input == null) {
                    throw new IllegalStateException(
                              "Configuration file not found: "
                                        + CONFIG_FILE);
               }

               configuration = objectMapper.readValue(
                         input,
                         EndpointConfiguration.class);

          } catch (IOException ex) {

               throw new IllegalStateException(
                         "Unable to load configuration file: "
                                   + CONFIG_FILE,
                         ex);

          }

     }

     private void validateConfiguration() {

          if (configuration == null) {
               throw new IllegalStateException(
                         "Configuration not loaded.");
          }

          if (configuration.getEndpoints() == null) {
               throw new IllegalStateException(
                         "No endpoints defined.");
          }

          Set<EndpointKey> keys = new HashSet<>();

          Set<String> ids = new HashSet<>();

          for (EndpointDefinition endpoint : configuration.getEndpoints()) {

               validateEndpoint(endpoint);
               
               if (!ids.add(endpoint.getId())) {
                    throw new IllegalStateException(
                              "Duplicate endpoint id: " + endpoint.getId());
               }

               EndpointKey key = endpointKey(
                         endpoint.getMethod(),
                         endpoint.getPath());

               if (!keys.add(key)) {
                    throw new IllegalStateException(
                              "Duplicate endpoint: "
                                        + key.method()
                                        + " "
                                        + key.path());
               }
          }

     }

     private void validateEndpoint(
               EndpointDefinition endpoint) {

          Objects.requireNonNull(
                    endpoint,
                    "Endpoint cannot be null");

          String method = normalizeMethod(
                    endpoint.getMethod());

          String path = normalizePath(
                    endpoint.getPath());

          validateForward(endpoint);

          if (!SUPPORTED_METHODS.contains(method)) {
               throw new IllegalStateException(
                         "Unsupported HTTP method: "
                                   + method);
          }

          endpoint.setMethod(method);
          endpoint.setPath(path);

     }

     private void buildIndex() {

          registry.clear();
          registryById.clear();

          for (EndpointDefinition endpoint : configuration.getEndpoints()) {

               if (registryById.containsKey(endpoint.getId())) {
                    throw new IllegalStateException(
                              "Duplicate endpoint id: " + endpoint.getId());
               }

               registry.put(
                         endpointKey(
                                   endpoint.getMethod(),
                                   endpoint.getPath()),
                         endpoint);

               registryById.put(
                         endpoint.getId(),
                         endpoint);
          }

     }

     public boolean exists(
               String method,
               String path) {

          return registry.containsKey(
                    endpointKey(
                              method,
                              path));
     }

     public EndpointDefinition get(
               String method,
               String path) {

          return registry.get(
                    endpointKey(
                              method,
                              path));
     }

     public EndpointDefinition get(String id, boolean required) {

          EndpointDefinition endpoint = registryById.get(id);

          if (required && endpoint == null) {
               throw new IllegalArgumentException(
                         "Endpoint not found: " + id);
          }

          return endpoint;
     }

     public Collection<EndpointDefinition> getEndpoints() {
          return Collections.unmodifiableCollection(
                    registry.values());
     }

     public EndpointDefinition get(String method, String path, boolean required) {

          EndpointDefinition endpoint = get(method, path);

          if (required && endpoint == null) {
               throw new IllegalArgumentException(
                         "Endpoint not found: "
                                   + normalizeMethod(method)
                                   + " "
                                   + normalizePath(path));
          }

          return endpoint;
     }

     public void updateForward(String id,
               EndpointForward forward) {

          EndpointDefinition endpoint = get(id, true);

          endpoint.setForward(forward);

          validateForward(endpoint);

          logger.info("Forward updated: {}",
                    endpoint.getId());
     }

     public void enableForward(String id) {

          EndpointDefinition endpoint = get(id, true);

          endpoint.getForward().setEnabled(true);

          validateForward(endpoint);

          logger.info("Forward enabled: {}",
                    endpoint.getId());
     }

     public void disableForward(String id) {

          EndpointDefinition endpoint = get(id, true);

          endpoint.getForward().setEnabled(false);

          logger.info("Forward disabled: {}",
                    endpoint.getId());
     }

     public int size() {
          return registry.size();
     }

     private EndpointKey endpointKey(
               String method,
               String path) {

          return new EndpointKey(
                    normalizeMethod(method),
                    normalizePath(path));
     }

     private String normalizeMethod(
               String method) {

          if (method == null || method.isBlank()) {
               throw new IllegalStateException(
                         "HTTP method cannot be null or blank.");
          }

          return method
                    .trim()
                    .toUpperCase(Locale.ROOT);
     }

     private String normalizePath(
               String path) {

          if (path == null || path.isBlank()) {
               throw new IllegalStateException(
                         "Path cannot be null or blank.");
          }

          String normalized = path.trim();

          if (!normalized.startsWith("/")) {
               normalized = "/" + normalized;
          }

          if (normalized.length() > 1 &&
                    normalized.endsWith("/")) {

               normalized = normalized.substring(
                         0,
                         normalized.length() - 1);
          }

          return normalized;
     }

     private void validateForward(EndpointDefinition endpoint) {

          EndpointForward forward = endpoint.getForward();

          // if (forward == null) {
          // return;
          // }

          if (!forward.isEnabled()) {
               return;
          }

          if (forward.getUrl() == null || forward.getUrl().isBlank()) {
               throw new IllegalStateException(
                         "Forward URL is mandatory when forward is enabled.");
          }

          try {
               URI.create(forward.getUrl());
          } catch (IllegalArgumentException ex) {
               throw new IllegalStateException(
                         "Invalid forward URL: " + forward.getUrl(), ex);
          }
     }
}