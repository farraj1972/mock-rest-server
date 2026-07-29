package com.mockserver.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mockserver.service.RequestProcessor;
import com.mockserver.util.ConsoleLogger;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MockController {

    private final RequestProcessor requestProcessor;

    public MockController(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {

        long startTime = System.nanoTime();

        ResponseEntity<String> response =
                requestProcessor.process(request);

        long elapsed = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime);

        ConsoleLogger.log(
                request,
                response.getBody(),
                elapsed);

        return response;
    }

}