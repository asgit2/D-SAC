package com.ghosh.deployment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String podName;
    private String namespace;
    private String status; // "Running", "Pending", "Failed", "CrashLoopBackOff", etc.
    private String containerStatus;
    private Integer restartCount;
    private String image;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id")
    private Deployment deployment;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastStatusUpdate;
    private String labels; // JSON string of labels
    private String annotations; // JSON string of annotations
}
