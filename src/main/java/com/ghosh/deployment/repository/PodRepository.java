package com.ghosh.deployment.repository;

import com.ghosh.deployment.model.Pod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PodRepository extends JpaRepository<Pod, Long> {
    List<Pod> findByClusterId(Long clusterId);
    
    List<Pod> findByDeploymentId(Long deploymentId);
    
    List<Pod> findByStatus(String status);
    
    List<Pod> findByClusterIdAndStatus(Long clusterId, String status);
}
