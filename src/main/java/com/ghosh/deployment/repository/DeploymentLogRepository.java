package com.ghosh.deployment.repository;

import com.ghosh.deployment.model.DeploymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeploymentLogRepository extends JpaRepository<DeploymentLog, Long> {
    List<DeploymentLog> findByDeploymentId(Long deploymentId);
    
    List<DeploymentLog> findByClusterId(Long clusterId);
    
    List<DeploymentLog> findByLogType(String logType);
    
    List<DeploymentLog> findByDeploymentIdAndLogType(Long deploymentId, String logType);
    
    List<DeploymentLog> findBySeverity(String severity);
}
