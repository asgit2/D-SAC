package com.ghosh.deployment.service;

import com.ghosh.deployment.dto.LogDTO;
import com.ghosh.deployment.model.DeploymentLog;
import com.ghosh.deployment.repository.DeploymentLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogService {

    private final DeploymentLogRepository logRepository;

    public LogService(DeploymentLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<LogDTO> getLogsByDeployment(Long deploymentId) {
        return logRepository.findByDeploymentId(deploymentId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LogDTO> getLogsByDeploymentAndType(Long deploymentId, String logType) {
        return logRepository.findByDeploymentIdAndLogType(deploymentId, logType).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LogDTO> getLogsByCluster(Long clusterId) {
        return logRepository.findByClusterId(clusterId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LogDTO> getLogsByType(String logType) {
        return logRepository.findByLogType(logType).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<LogDTO> getErrorLogs() {
        return logRepository.findBySeverity("ERROR").stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void savePodLog(Long deploymentId, Long clusterId, String podName, 
                          String logContent, String severity) {
        DeploymentLog deploymentLog = DeploymentLog.builder()
            .logType("CONTAINER_LOG")
            .logContent(logContent)
            .source(podName)
            .severity(severity)
            .createdAt(LocalDateTime.now())
            .logTimestamp(LocalDateTime.now())
            .build();
        
        logRepository.save(deploymentLog);
        log.info("Saved log for pod: {}", podName);
    }

    @Transactional
    public void saveApiLog(Long deploymentId, String logContent, String severity) {
        DeploymentLog deploymentLog = DeploymentLog.builder()
            .logType("API_LOG")
            .logContent(logContent)
            .severity(severity)
            .createdAt(LocalDateTime.now())
            .logTimestamp(LocalDateTime.now())
            .build();
        
        logRepository.save(deploymentLog);
    }

    @Transactional
    public void saveAgentLog(Long deploymentId, String logContent, String severity) {
        DeploymentLog deploymentLog = DeploymentLog.builder()
            .logType("AGENT_LOG")
            .logContent(logContent)
            .severity(severity)
            .createdAt(LocalDateTime.now())
            .logTimestamp(LocalDateTime.now())
            .build();
        
        logRepository.save(deploymentLog);
    }

    private LogDTO convertToDTO(DeploymentLog log) {
        return LogDTO.builder()
            .id(log.getId())
            .logType(log.getLogType())
            .logContent(log.getLogContent())
            .source(log.getSource())
            .severity(log.getSeverity())
            .createdAt(log.getCreatedAt())
            .logTimestamp(log.getLogTimestamp())
            .deploymentName(log.getDeployment() != null ? log.getDeployment().getDeploymentName() : null)
            .clusterName(log.getCluster() != null ? log.getCluster().getName() : null)
            .build();
    }
}
