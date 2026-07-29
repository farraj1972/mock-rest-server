package com.mockserver.controller;

import com.mockserver.util.ConsoleLogger;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MockController {

    @RequestMapping(
            value = {"/", "/**"},
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH,
                    RequestMethod.DELETE
            }
    )
    public ResponseEntity<Map<String, String>> handle(
            HttpServletRequest request,
            @RequestBody(required = false) String body) {

        long start = System.currentTimeMillis();

        ResponseEntity<Map<String, String>> response =
                ResponseEntity.ok(
                        Map.of(
                                "status",
                                "OK"
                        )
                );

        ConsoleLogger.log(
                request,
                body,
                System.currentTimeMillis() - start);

        return response;
    }
}