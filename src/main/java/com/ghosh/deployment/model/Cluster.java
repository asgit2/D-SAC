package com.ghosh.deployment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "clusters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cluster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String kubeConfigPath;
    private String apiServerUrl;
    private String status; // "connected", "disconnected", "error"
    private LocalDateTime lastConnectionCheck;
    private String description;
}
