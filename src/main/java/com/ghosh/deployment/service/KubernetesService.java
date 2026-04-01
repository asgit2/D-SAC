package com.ghosh.deployment.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KubernetesService {

    private final ApiClient apiClient;

    public KubernetesService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<V1Pod> getPodsByCluster(String namespace) {
        try {
            CoreV1Api api = new CoreV1Api(apiClient);
            V1PodList podList = api.listNamespacedPod(namespace).execute();
            return podList.getItems();
        } catch (Exception e) {
            log.error("Error fetching pods from cluster for namespace: {}", namespace, e);
            return new ArrayList<>();
        }
    }

    public List<V1Deployment> getDeploymentsByCluster(String namespace) {
        try {
            AppsV1Api api = new AppsV1Api(apiClient);
            V1DeploymentList deploymentList = api.listNamespacedDeployment(namespace).execute();
            return deploymentList.getItems();
        } catch (Exception e) {
            log.error("Error fetching deployments from cluster for namespace: {}", namespace, e);
            return new ArrayList<>();
        }
    }

    public String getPodLogs(String namespace, String podName, String containerName) {
        try {
            CoreV1Api api = new CoreV1Api(apiClient);
            return api.readNamespacedPodLog(podName, namespace).execute();
        } catch (Exception e) {
            log.error("Error fetching logs for pod: {}/{}", namespace, podName, e);
            return "Error fetching logs: " + e.getMessage();
        }
    }

    public boolean checkClusterConnectivity() {
        try {
            CoreV1Api api = new CoreV1Api(apiClient);
            api.listNamespacedPod("default").execute();
            return true;
        } catch (Exception e) {
            log.error("Error connecting to Kubernetes cluster", e);
            return false;
        }
    }

    public List<String> getAllNamespaces() {
        try {
            CoreV1Api api = new CoreV1Api(apiClient);
            V1NamespaceList namespaceList = api.listNamespace().execute();
            List<String> namespaces = new ArrayList<>();
            for (V1Namespace ns : namespaceList.getItems()) {
                namespaces.add(ns.getMetadata().getName());
            }
            return namespaces;
        } catch (Exception e) {
            log.error("Error fetching namespaces from cluster", e);
            return new ArrayList<>();
        }
    }
}
