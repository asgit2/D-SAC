# Deployment Stack Admin Console

A comprehensive Spring Boot-based Kubernetes cluster monitoring and log management console that provides real-time visibility into pod statuses, deployment health, and centralized log aggregation across multiple Kubernetes clusters.

## Features

- **Multi-Cluster Support**: Monitor and manage multiple Kubernetes clusters from a single console
- **Pod Status Monitoring**: Real-time pod status tracking with detailed health information
- **Deployment Tracking**: Monitor deployment replicas, updates, and availability
- **Failed Deployment Detection**: Automatic identification and alerting for failed deployments
- **Log Aggregation**: Centralized log collection from multiple sources:
  - API Logs
  - Agent Logs
  - Container Logs
  - System Logs
  - Application Logs
- **Log Download**: Selective log download by type, deployment, or cluster
- **Bootstrap Dashboard**: Responsive web UI for easy navigation and monitoring
- **RESTful API**: Complete REST API for programmatic access to deployment and log data

## Architecture

### Backend Components
- **Spring Boot 3.2**: REST API and core application logic
- **Kubernetes Java Client**: Direct integration with Kubernetes API
- **JPA/Hibernate**: Data persistence and querying
- **H2 Database**: In-memory database for development (configurable for production)

### Frontend
- **Bootstrap 5**: Responsive UI framework
- **Bootstrap Icons**: Icon library for visual indicators
- **Vanilla JavaScript**: Interactive dashboard functionality

## Prerequisites

- Java 17 or higher
- Maven 3.8.0 or higher
- Kubernetes cluster(s) accessible via kubeconfig
- kubectl configured with cluster access

## Installation

### 1. Clone or Extract the Project
```bash
cd /Users/abhishek/code/Ghosh-tenable-connector
```

### 2. Configure Kubernetes Access
Ensure your `~/.kube/config` file is properly configured with your cluster credentials:
```bash
# Verify kubeconfig
kubectl cluster-info
```

### 3. Build the Application
```bash
mvn clean package
```

### 4. Run the Application

#### Option A: Using Maven
```bash
mvn spring-boot:run
```

#### Option B: Using Built JAR
```bash
java -jar target/deployment-stack-console-1.0.0.jar
```

The application will start on `http://localhost:8080`

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Server Port
server.port=8080

# Database (configure for production)
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Kubernetes Settings
kubernetes.namespace.default=default
kubernetes.sync.interval=30000

# Logging
logging.level.com.tenable=DEBUG
```

## API Endpoints

### Clusters
- `GET /api/clusters` - List all clusters
- `GET /api/clusters/{id}` - Get cluster details
- `POST /api/clusters` - Add a new cluster
- `PUT /api/clusters/{id}` - Update cluster
- `GET /api/clusters/{id}/status` - Check cluster health

### Deployments
- `GET /api/deployments` - Get all failed deployments
- `GET /api/deployments/cluster/{clusterId}` - Get deployments by cluster
- `GET /api/deployments/{id}` - Get deployment details

### Logs
- `GET /api/logs/deployment/{deploymentId}` - Get all logs for deployment
- `GET /api/logs/deployment/{deploymentId}/type/{logType}` - Get logs by type
- `GET /api/logs/cluster/{clusterId}` - Get logs by cluster
- `GET /api/logs/type/{logType}` - Get logs by type
- `GET /api/logs/errors` - Get error logs
- `GET /api/logs/deployment/{deploymentId}/download` - Download deployment logs
- `GET /api/logs/deployment/{deploymentId}/type/{logType}/download` - Download logs by type

## Using the Console

### 1. Access the Web Dashboard
Navigate to `http://localhost:8080` in your browser

### 2. Select a Cluster
- Use the cluster dropdown to select which cluster to monitor
- The console displays connection status indicator

### 3. View Deployments
- **Failed Deployments** section shows all deployments with issues
- **All Deployments** section shows detailed status for all deployments
- Click "View Details" to see more information

### 4. Download Logs
- Click on a failed deployment to open the detail view
- Select log type buttons to view logs:
  - API Logs
  - Agent Logs
  - Container Logs
- Click "Download All" to download all logs as a text file

### 5. Monitor Pods
- Pods are displayed with status indicators:
  - 🟢 Running (green)
  - 🔴 Failed (red)
  - 🟡 Pending (yellow)
  - ⚪ Unknown (gray)

## Dashboard Sections

### Status Indicator
- **Green Circle**: Connected to cluster
- **Yellow Circle**: Disconnected
- **Red Circle**: Error state

### Statistics Cards
- **Total Deployments**: Count of all deployments in selected cluster
- **Failed Deployments**: Count of deployments with issues
- **Total Pods**: Count of all running pods
- **Failed Pods**: Count of pods in failed state

### Failed Deployments Alert
Displays all deployments that are not in healthy state with quick access to logs and details

### Deployment Details Modal
When viewing deployment details:
- Full deployment status and replica counts
- Log filtering by type
- Individual log entries with timestamps and severity
- One-click download of all logs

## Docker Deployment

### Build Docker Image
```dockerfile
FROM maven:3.8-eclipse-temurin-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -q

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/deployment-stack-console-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build and Run Docker Container
```bash
# Build
docker build -t deployment-stack-console:1.0.0 .

# Run with kubeconfig
docker run -p 8080:8080 \
  -v ~/.kube:/root/.kube \
  deployment-stack-console:1.0.0
```

### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment-stack-console
spec:
  replicas: 1
  selector:
    matchLabels:
      app: deployment-console
  template:
    metadata:
      labels:
        app: deployment-console
    spec:
      serviceAccountName: deployment-console
      containers:
      - name: console
        image: deployment-stack-console:1.0.0
        ports:
        - containerPort: 8080
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: deployment-console
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  name: deployment-console
rules:
- apiGroups: ["apps"]
  resources: ["deployments", "statefulsets", "daemonsets"]
  verbs: ["get", "list", "watch"]
- apiGroups: [""]
  resources: ["pods", "pods/log", "namespaces"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: deployment-console
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: deployment-console
subjects:
- kind: ServiceAccount
  name: deployment-console
  namespace: default
```

## Extending the Application

### Adding New Log Types
1. Update `DeploymentLog.java` model to support new log types
2. Add new methods in `LogService` for the new log type
3. Update the UI buttons in `index.html` to include the new log type

### Multi-Database Support
Replace H2 with PostgreSQL/MySQL by updating:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/deployment_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Security
Add Spring Security for authentication:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Troubleshooting

### Issue: Cannot connect to Kubernetes cluster
- Verify kubeconfig: `kubectl cluster-info`
- Check file permissions: `chmod 600 ~/.kube/config`
- Ensure KUBECONFIG environment variable is set correctly

### Issue: Empty deployments list
- Ensure cluster is selected
- Check cluster connectivity status indicator
- Verify RBAC permissions for service account

### Issue: Logs not appearing
- Ensure pods have logging enabled
- Check pod logs directly: `kubectl logs <pod-name> -n <namespace>`
- Verify log aggregation interval setting

## Support

For issues or feature requests, check the logs:
```bash
# View application logs
tail -f logs/application.log

# Or check Spring Boot startup logs
kubectl logs -f <pod-name> -n default
```

## License

MIT License - See LICENSE file for details

## Contributing

Contributions are welcome! Please fork the repository and submit pull requests.
