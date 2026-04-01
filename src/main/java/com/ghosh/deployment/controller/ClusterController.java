package com.ghosh.deployment.controller;

import com.ghosh.deployment.dto.ClusterDTO;
import com.ghosh.deployment.model.Cluster;
import com.ghosh.deployment.repository.ClusterRepository;
import com.ghosh.deployment.repository.DeploymentRepository;
import com.ghosh.deployment.repository.PodRepository;
import com.ghosh.deployment.service.KubernetesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clusters")
@Slf4j
public class ClusterController {

    private final ClusterRepository clusterRepository;
    private final DeploymentRepository deploymentRepository;
    private final PodRepository podRepository;
    private final KubernetesService kubernetesService;

    public ClusterController(ClusterRepository clusterRepository,
                           DeploymentRepository deploymentRepository,
                           PodRepository podRepository,
                           KubernetesService kubernetesService) {
        this.clusterRepository = clusterRepository;
        this.deploymentRepository = deploymentRepository;
        this.podRepository = podRepository;
        this.kubernetesService = kubernetesService;
    }

    @GetMapping
    public ResponseEntity<List<ClusterDTO>> getAllClusters() {
        try {
            List<ClusterDTO> clusters = clusterRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(clusters);
        } catch (Exception e) {
            log.error("Error fetching clusters", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClusterDTO> getClusterById(@PathVariable Long id) {
        try {
            return clusterRepository.findById(id)
                .map(cluster -> ResponseEntity.ok(convertToDTO(cluster)))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching cluster: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<ClusterDTO> addCluster(@RequestBody Cluster cluster) {
        try {
            Cluster saved = clusterRepository.save(cluster);
            return ResponseEntity.ok(convertToDTO(saved));
        } catch (Exception e) {
            log.error("Error adding cluster", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClusterDTO> updateCluster(@PathVariable Long id, @RequestBody Cluster cluster) {
        try {
            return clusterRepository.findById(id)
                .map(existing -> {
                    existing.setName(cluster.getName());
                    existing.setKubeConfigPath(cluster.getKubeConfigPath());
                    existing.setApiServerUrl(cluster.getApiServerUrl());
                    existing.setDescription(cluster.getDescription());
                    Cluster updated = clusterRepository.save(existing);
                    return ResponseEntity.ok(convertToDTO(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error updating cluster: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> checkClusterStatus(@PathVariable Long id) {
        try {
            return clusterRepository.findById(id)
                .map(cluster -> {
                    boolean isConnected = kubernetesService.checkClusterConnectivity();
                    return ResponseEntity.ok(isConnected ? "healthy" : "disconnected");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error checking cluster status: {}", id, e);
            return ResponseEntity.status(500).build();
        }
    }

    private ClusterDTO convertToDTO(Cluster cluster) {
        long podsCount = podRepository.findByClusterId(cluster.getId()).size();
        long deploymentsCount = deploymentRepository.findByClusterId(cluster.getId()).size();
        long failedPodsCount = podRepository.findByClusterIdAndStatus(cluster.getId(), "Failed").size();

        return ClusterDTO.builder()
            .id(cluster.getId())
            .name(cluster.getName())
            .status(cluster.getStatus())
            .lastConnectionCheck(cluster.getLastConnectionCheck())
            .description(cluster.getDescription())
            .podsCount((int) podsCount)
            .deploymentsCount((int) deploymentsCount)
            .failedPodsCount((int) failedPodsCount)
            .build();
    }
}
