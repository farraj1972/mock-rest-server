package com.mockserver.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mockserver.model.EndpointDefinition;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RequestProcessor {

     private final EndpointRegistry endpointRegistry;

     private final MockService mockService;

     private final ProxyService proxyService;

     public RequestProcessor(EndpointRegistry endpointRegistry,
               MockService mockService,
               ProxyService proxyService) {

          this.endpointRegistry = endpointRegistry;
          this.mockService = mockService;
          this.proxyService = proxyService;
     }

     public ResponseEntity<String> process(HttpServletRequest request) {

          EndpointDefinition endpoint = endpointRegistry.get(
                    request.getMethod(),
                    request.getRequestURI());

          if (endpoint == null) {

               return ResponseEntity
                         .status(HttpStatus.NOT_FOUND)
                         .body("""
                                   {
                                     "error": "Endpoint not configured"
                                   }
                                   """);
          }

          if (endpoint.getForward().isEnabled()) {
               return proxyService.execute(request, endpoint);
          }

          return mockService.execute(endpoint);
     }

}