package com.ghosh.deployment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "deployment_logs", indexes = {
    @Index(name = "idx_deployment_id", columnList = "deployment_id"),
    @Index(name = "idx_cluster_id", columnList = "cluster_id"),
    @Index(name = "idx_log_type", columnList = "log_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id")
    private Deployment deployment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;
    
    private String logType; // "API_LOG", "AGENT_LOG", "SYSTEM_LOG", "APPLICATION_LOG", "CONTAINER_LOG"
    private String logContent;
    private String source; // pod name, service name, etc.
    private String severity; // "INFO", "WARNING", "ERROR", "DEBUG"
    private LocalDateTime createdAt;
    private LocalDateTime logTimestamp;
}
