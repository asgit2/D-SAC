# Getting Started with Deployment Stack Console

## Quick Start (5 minutes)

### 1. Prerequisites
```bash
# Verify Java 17+
java -version

# Verify Maven 3.8+
mvn -version

# Verify kubectl is installed
kubectl version --client

# Verify kubeconfig is configured
kubectl cluster-info
```

### 2. Build the Application
```bash
cd /Users/abhishek/code/Ghosh-tenable-connector
mvn clean package
```

### 3. Run the Application
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using the built JAR
java -jar target/deployment-stack-console-1.0.0.jar

# Option 3: Using the startup script
./start.sh
```

### 4. Open the Dashboard
Navigate to: **http://localhost:8080**

You should see:
- ✓ Cluster selector dropdown
- ✓ Statistics cards (deployments, pods, failed)
- ✓ Failed deployments section
- ✓ All deployments list
- ✓ Connection status indicator

---

## Detailed Usage Guide

### Connecting to Your Cluster

1. **Select a Cluster**
   - Click the cluster dropdown
   - Choose your Kubernetes cluster
   - Status indicator should turn green if connected

2. **View Deployments**
   - "All Deployments" section shows every deployment
   - Click "View Details" to see:
     - Pod replica counts
     - Deployment status
     - Last update time
     - Available/ready replicas

### Monitoring Failed Deployments

The **Failed Deployments** section automatically shows:
- All deployments not in "Successful" state
- Real-time updates every 30 seconds
- Direct link to download logs

### Downloading Logs

For any failed deployment:

1. Click "View & Download Logs" button
2. Select log type:
   - **API Logs** - API server logs
   - **Agent Logs** - Agent/controller logs
   - **Container Logs** - Pod container logs
3. Click "Download All" to get combined log file

Logs are formatted as:
```
[timestamp] [type] [severity] [cluster]
Source: pod-name
log-content
```

---

## Configuration Guide

### Port Configuration
Edit `src/main/resources/application.properties`:
```properties
server.port=8080  # Change to any available port
```

### Database Configuration (Production)
Edit `application.properties`:
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/deployment_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

### Kubernetes Settings
```properties
kubernetes.namespace.default=default  # Default namespace to monitor
kubernetes.sync.interval=30000        # Refresh interval in ms
```

### Logging Configuration
```properties
logging.level.com.tenable=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate=WARN
```

---

## Docker Deployment

### Build and Run with Docker Compose
```bash
cd /Users/abhishek/code/Ghosh-tenable-connector

# Build and start
docker-compose up

# Stop
docker-compose down
```

### Build Docker Image Manually
```bash
docker build -t deployment-console:1.0.0 .

# Run with Kubernetes access
docker run -p 8080:8080 \
  -v ~/.kube:/root/.kube \
  deployment-console:1.0.0
```

### Push to Registry
```bash
# Tag the image
docker tag deployment-console:1.0.0 myregistry/deployment-console:1.0.0

# Push to registry
docker push myregistry/deployment-console:1.0.0
```

---

## Deploying to Kubernetes

### Create Namespace
```bash
kubectl create namespace deployment-console
```

### Apply the deployment
```bash
kubectl apply -f - << 'EOF'
apiVersion: v1
kind: ServiceAccount
metadata:
  name: deployment-console
  namespace: deployment-console

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
  namespace: deployment-console

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment-stack-console
  namespace: deployment-console
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
        image: deployment-console:1.0.0
        imagePullPolicy: IfNotPresent
        ports:
        - name: http
          containerPort: 8080
        env:
        - name: KEY
          value: value
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"

---
apiVersion: v1
kind: Service
metadata:
  name: deployment-console-service
  namespace: deployment-console
spec:
  type: NodePort
  selector:
    app: deployment-console
  ports:
  - protocol: TCP
    port: 8080
    targetPort: 8080
    nodePort: 30080
EOF
```

### Access the Service
```bash
# Get the service IP
kubectl get svc -n deployment-console

# Port forward for local access
kubectl port-forward svc/deployment-console-service 8080:8080 -n deployment-console

# Access at http://localhost:8080
```

---

## Troubleshooting

### Issue: "Cannot connect to cluster"
**Solution:**
```bash
# Verify kubeconfig
kubectl cluster-info

# Check file permissions
chmod 600 ~/.kube/config

# View logs
tail -f logs/application.log
```

### Issue: Empty deployments list
**Solution:**
- Ensure cluster is selected
- Check status indicator is green
- Verify RBAC permissions:
  ```bash
  kubectl auth can-i list deployments --as=system:serviceaccount:default:default
  ```

### Issue: Port 8080 already in use
**Solution:**
```bash
# Use different port
java -jar target/deployment-stack-console-1.0.0.jar --server.port=8081
```

### Issue: Logs not appearing
**Solution:**
```bash
# Verify pod logs exist
kubectl logs <pod-name> -n <namespace>

# Check application logs
grep "ERROR" logs/application.log
```

---

## API Examples

### Get all clusters
```bash
curl http://localhost:8080/api/clusters
```

### Get deployments for cluster
```bash
curl http://localhost:8080/api/deployments/cluster/1
```

### Get failed deployments
```bash
curl http://localhost:8080/api/deployments
```

### Download logs
```bash
curl -o deployment_logs.txt \
  http://localhost:8080/api/logs/deployment/1/download

curl -o api_logs.txt \
  http://localhost:8080/api/logs/deployment/1/type/API_LOG/download
```

---

## Performance Tips

1. **Database**: Use PostgreSQL for production (better performance than H2)
2. **Sync Interval**: Adjust `kubernetes.sync.interval` based on load
3. **Memory**: Increase with `-Xmx1024m` flag for large clusters
4. **Caching**: Add Spring Cache for frequently accessed data
5. **Indexing**: Ensure database indexes on cluster_id, deployment_id for logs

---

## Next Steps

1. **Customize UI**: Edit `static/index.html` to add your branding
2. **Add Security**: Implement Spring Security for authentication
3. **Extend API**: Add new endpoints in `controller/` package
4. **Enhance Logs**: Add more log types in `model/DeploymentLog.java`
5. **Scale**: Deploy to Kubernetes for multi-cluster monitoring

---

## Support & Documentation

- Full documentation: See `README.md`
- API docs: See `README.md` - API Endpoints section
- Architecture: See `README.md` - Architecture section
- Configuration: See `Application.properties` comments
