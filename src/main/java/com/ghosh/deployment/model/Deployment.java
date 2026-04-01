package com.ghosh.deployment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "deployments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deployment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deploymentName;
    private String namespace;
    private String status; // "Successful", "Failed", "In Progress", "Rollback"
    private Integer replicas;
    private Integer readyReplicas;
    private Integer updatedReplicas;
    private Integer availableReplicas;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;
    
    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL)
    private List<Pod> pods;
    
    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL)
    private List<DeploymentLog> logs;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdateTime;
    private String conditions; // JSON string of deployment conditions
    private String image;
}
