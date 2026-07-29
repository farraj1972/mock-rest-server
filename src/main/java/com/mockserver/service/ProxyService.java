package com.mockserver.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mockserver.model.EndpointDefinition;

@Service
public class ProxyService {

    public ResponseEntity<String> execute(EndpointDefinition endpoint) {

        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body("""
                      {
                        "error": "Proxy mode not implemented"
                      }
                      """);
    }

}