package com.ghosh.deployment.controller;

import com.ghosh.deployment.dto.LogDTO;
import com.ghosh.deployment.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@Slf4j
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/deployment/{deploymentId}")
    public ResponseEntity<List<LogDTO>> getLogsByDeployment(@PathVariable Long deploymentId) {
        try {
            List<LogDTO> logs = logService.getLogsByDeployment(deploymentId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs for deployment: {}", deploymentId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/deployment/{deploymentId}/type/{logType}")
    public ResponseEntity<List<LogDTO>> getLogsByDeploymentAndType(
            @PathVariable Long deploymentId,
            @PathVariable String logType) {
        try {
            List<LogDTO> logs = logService.getLogsByDeploymentAndType(deploymentId, logType);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs of type: {} for deployment: {}", logType, deploymentId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/cluster/{clusterId}")
    public ResponseEntity<List<LogDTO>> getLogsByCluster(@PathVariable Long clusterId) {
        try {
            List<LogDTO> logs = logService.getLogsByCluster(clusterId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs for cluster: {}", clusterId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/type/{logType}")
    public ResponseEntity<List<LogDTO>> getLogsByType(@PathVariable String logType) {
        try {
            List<LogDTO> logs = logService.getLogsByType(logType);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs of type: {}", logType, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/errors")
    public ResponseEntity<List<LogDTO>> getErrorLogs() {
        try {
            List<LogDTO> logs = logService.getErrorLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching error logs", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/deployment/{deploymentId}/download")
    public ResponseEntity<byte[]> downloadDeploymentLogs(@PathVariable Long deploymentId) {
        try {
            List<LogDTO> logs = logService.getLogsByDeployment(deploymentId);
            String logContent = logs.stream()
                .map(log -> formatLogForDownload(log))
                .collect(Collectors.joining("\n" + "=".repeat(100) + "\n"));

            byte[] bytes = logContent.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", 
                "deployment_" + deploymentId + "_logs_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log");

            return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
        } catch (Exception e) {
            log.error("Error downloading logs for deployment: {}", deploymentId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/deployment/{deploymentId}/type/{logType}/download")
    public ResponseEntity<byte[]> downloadDeploymentLogsByType(
            @PathVariable Long deploymentId,
            @PathVariable String logType) {
        try {
            List<LogDTO> logs = logService.getLogsByDeploymentAndType(deploymentId, logType);
            String logContent = logs.stream()
                .map(log -> formatLogForDownload(log))
                .collect(Collectors.joining("\n" + "=".repeat(100) + "\n"));

            byte[] bytes = logContent.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", 
                "deployment_" + deploymentId + "_" + logType + "_logs_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".log");

            return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
        } catch (Exception e) {
            log.error("Error downloading {} logs for deployment: {}", logType, deploymentId, e);
            return ResponseEntity.status(500).build();
        }
    }

    private String formatLogForDownload(LogDTO log) {
        return String.format(
            "[%s] [%s] [%s] [%s]\nSource: %s\n%s",
            log.getLogTimestamp(),
            log.getLogType(),
            log.getSeverity(),
            log.getClusterName(),
            log.getSource(),
            log.getLogContent()
        );
    }
}
