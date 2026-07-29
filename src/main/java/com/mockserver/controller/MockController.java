package com.mockserver.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockserver.model.EndpointDefinition;
import com.mockserver.service.EndpointRegistry;
import com.mockserver.util.ConsoleLogger;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MockController {

    private final EndpointRegistry endpointRegistry;

    public MockController(EndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {

        long startTime = System.nanoTime();

        EndpointDefinition endpoint = endpointRegistry.get(
                request.getMethod(),
                request.getRequestURI());

        if (endpoint == null) {
            return respond(
                    request,
                    HttpStatus.NOT_FOUND,
                    """
                    {
                      "error": "Endpoint not configured"
                    }
                    """,
                    startTime);
        }

        return respond(
                request,
                HttpStatus.OK,
                """
                {
                  "status": "OK"
                }
                """,
                startTime);
    }

    private ResponseEntity<String> respond(HttpServletRequest request,
                                           HttpStatus status,
                                           String body,
                                           long startTime) {

        long elapsed = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime);

        ConsoleLogger.log(request, body, elapsed);

        return ResponseEntity
                .status(status)
                .body(body);
    }
}