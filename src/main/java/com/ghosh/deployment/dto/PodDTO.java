package com.ghosh.deployment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodDTO {
    private Long id;
    private String podName;
    private String namespace;
    private String status;
    private String containerStatus;
    private Integer restartCount;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime lastStatusUpdate;
    private String clusterId;
    private String clusterName;
    private String deploymentName;
}
