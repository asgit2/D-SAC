package com.ghosh.deployment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentDTO {
    private Long id;
    private String deploymentName;
    private String namespace;
    private String status;
    private Integer replicas;
    private Integer readyReplicas;
    private Integer updatedReplicas;
    private Integer availableReplicas;
    private String clusterId;
    private String clusterName;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdateTime;
    private List<PodDTO> pods;
    private Boolean isFailed;
}
