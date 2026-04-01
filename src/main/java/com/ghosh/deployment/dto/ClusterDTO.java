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
public class ClusterDTO {
    private Long id;
    private String name;
    private String status;
    private LocalDateTime lastConnectionCheck;
    private String description;
    private Integer podsCount;
    private Integer deploymentsCount;
    private Integer failedPodsCount;
}
