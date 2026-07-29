package com.mockserver.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mockserver.model.EndpointDefinition;

@Service
public class MockService {

    public ResponseEntity<String> execute(EndpointDefinition endpoint) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("""
                      {
                        "status": "OK"
                      }
                      """);
    }

}