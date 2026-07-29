package com.mockserver.service;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.mockserver.model.EndpointDefinition;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProxyService {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade",
            "host",
            "content-length");

    private final RestTemplate restTemplate;

    public ProxyService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public ResponseEntity<String> execute(HttpServletRequest request,
                                          EndpointDefinition endpoint) {

        try {

            String targetUrl = buildTargetUrl(request, endpoint);

            HttpHeaders headers = copyHeaders(request);

            HttpEntity<String> entity = new HttpEntity<>(
                    readBody(request),
                    headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    String.class);

            return ResponseEntity
                    .status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());

        } catch (ResourceAccessException ex) {

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("""
                        {
                          "error":"Unable to reach upstream server"
                        }
                        """);
        }
    }

    private String buildTargetUrl(HttpServletRequest request,
                                  EndpointDefinition endpoint) {

        String base = endpoint.getForward().getUrl();

        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String url = base + request.getRequestURI();

        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }

        return url;
    }

    private HttpHeaders copyHeaders(HttpServletRequest request) {

        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> names = request.getHeaderNames();

        while (names.hasMoreElements()) {

            String name = names.nextElement();

            if (HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                continue;
            }

            headers.put(
                    name,
                    Collections.list(request.getHeaders(name)));
        }

        return headers;
    }

    private String readBody(HttpServletRequest request) {

        try {
            return StreamUtils.copyToString(
                    request.getInputStream(),
                    java.nio.charset.StandardCharsets.UTF_8);

        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read request body", ex);
        }
    }

}