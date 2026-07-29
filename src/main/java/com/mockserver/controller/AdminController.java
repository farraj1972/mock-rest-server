package com.mockserver.controller;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mockserver.model.EndpointDefinition;
import com.mockserver.model.EndpointForward;
import com.mockserver.service.AdminService;

@RestController
@RequestMapping("/admin/endpoints")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Collection<EndpointDefinition> getEndpoints() {
        return adminService.getEndpoints();
    }

    @GetMapping("/{id}")
    public EndpointDefinition getEndpoint(
            @PathVariable String id) {

        return adminService.getEndpoint(id);
    }

    @PutMapping("/{id}/forward")
    public ResponseEntity<Void> updateForward(
            @PathVariable String id,
            @RequestBody EndpointForward forward) {

        adminService.updateForward(id, forward);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Void> enableForward(
            @PathVariable String id) {

        adminService.enableForward(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Void> disableForward(
            @PathVariable String id) {

        adminService.disableForward(id);

        return ResponseEntity.noContent().build();
    }

}