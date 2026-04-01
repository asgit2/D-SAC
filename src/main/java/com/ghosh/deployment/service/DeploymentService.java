package com.ghosh.deployment.service;

import com.ghosh.deployment.dto.DeploymentDTO;
import com.ghosh.deployment.model.Deployment;
import com.ghosh.deployment.repository.DeploymentRepository;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;

    public DeploymentService(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    public List<DeploymentDTO> getDeploymentsByCluster(Long clusterId) {
        return deploymentRepository.findByClusterId(clusterId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<DeploymentDTO> getFailedDeployments() {
        return deploymentRepository.findFailedDeployments().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Optional<DeploymentDTO> getDeploymentById(Long id) {
        return deploymentRepository.findById(id)
            .map(this::convertToDTO);
    }

    @Transactional
    public void syncDeployments(Long clusterId, List<V1Deployment> k8sDeployments) {
        for (V1Deployment k8sDeploy : k8sDeployments) {
            String name = k8sDeploy.getMetadata().getName();
            String namespace = k8sDeploy.getMetadata().getNamespace();
            
            Optional<Deployment> existing = deploymentRepository
                .findByClusterId(clusterId).stream()
                .filter(d -> d.getDeploymentName().equals(name) && d.getNamespace().equals(namespace))
                .findFirst();

            Deployment deployment = existing.orElse(new Deployment());
            
            deployment.setDeploymentName(name);
            deployment.setNamespace(namespace);
            deployment.setLastUpdateTime(LocalDateTime.now());
            
            if (k8sDeploy.getSpec() != null) {
                V1DeploymentSpec spec = k8sDeploy.getSpec();
                deployment.setReplicas(spec.getReplicas());
            }
            
            if (k8sDeploy.getStatus() != null) {
                V1DeploymentStatus status = k8sDeploy.getStatus();
                deployment.setStatus(status.getConditions() != null 
                    && status.getConditions().stream()
                        .anyMatch(c -> "Available".equals(c.getType()) && "True".equals(c.getStatus()))
                    ? "Successful" : "Failed");
                deployment.setReadyReplicas(status.getReadyReplicas());
                deployment.setUpdatedReplicas(status.getUpdatedReplicas());
                deployment.setAvailableReplicas(status.getAvailableReplicas());
            }
            
            deploymentRepository.save(deployment);
        }
    }

    private DeploymentDTO convertToDTO(Deployment deployment) {
        return DeploymentDTO.builder()
            .id(deployment.getId())
            .deploymentName(deployment.getDeploymentName())
            .namespace(deployment.getNamespace())
            .status(deployment.getStatus())
            .replicas(deployment.getReplicas())
            .readyReplicas(deployment.getReadyReplicas())
            .updatedReplicas(deployment.getUpdatedReplicas())
            .availableReplicas(deployment.getAvailableReplicas())
            .clusterId(deployment.getCluster().getId().toString())
            .clusterName(deployment.getCluster().getName())
            .createdAt(deployment.getCreatedAt())
            .lastUpdateTime(deployment.getLastUpdateTime())
            .isFailed(deployment.getStatus() != null && deployment.getStatus().equals("Failed"))
            .build();
    }
}
