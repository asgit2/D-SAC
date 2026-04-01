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
public class LogDTO {
    private Long id;
    private String logType;
    private String logContent;
    private String source;
    private String severity;
    private LocalDateTime createdAt;
    private LocalDateTime logTimestamp;
    private String deploymentName;
    private String clusterName;
}
