package com.ghosh.deployment.controller;

import com.ghosh.deployment.dto.DeploymentDTO;
import com.ghosh.deployment.service.DeploymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deployments")
@Slf4j
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @GetMapping
    public ResponseEntity<List<DeploymentDTO>> getAllFailedDeployments() {
        try {
            List<DeploymentDTO> deployments = deploymentService.getFailedDeployments();
            return ResponseEntity.ok(deployments);
        } catch (Exception e) {
            log.error("Error fetching failed deployments", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/cluster/{clusterId}")
    public ResponseEntity<List<DeploymentDTO>> getDeploymentsByCluster(@PathVariable Long clusterId) {
        try {
            List<DeploymentDTO> deployments = deploymentService.getDeploymentsByCluster(clusterId);
            return ResponseEntity.ok(deployments);
        } catch (Exception e) {
            log.error("Error fetching deployments for cluster: {}", clusterId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeploymentDTO> getDeploymentById(@PathVariable Long id) {
        try {
            return deploymentService.getDeploymentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching deployment: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }
}
