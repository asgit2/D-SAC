package com.ghosh.deployment.repository;

import com.ghosh.deployment.model.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByClusterId(Long clusterId);
    
    List<Deployment> findByStatus(String status);
    
    List<Deployment> findByClusterIdAndStatus(Long clusterId, String status);
    
    @Query("SELECT d FROM Deployment d WHERE d.status = 'Failed' OR d.availableReplicas < d.replicas")
    List<Deployment> findFailedDeployments();
}
