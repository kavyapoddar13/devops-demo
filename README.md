# DevOps Self-Learning Activity Project (24CYAEC59)
**Department of CSE (Cyber Security) & CSE (AI & ML) — MSRIT**

This repository contains the complete implementation for **Component 1** and **Component 2** of the DevOps Project-Based Learning Activity.

---

## Project Structure Overview

```
devops-demo/
├── pom.xml                               # Maven project descriptor (Spring Boot 3.2.x, Java 17)
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java      # Main Spring Boot Application
│   │   │   └── HelloController.java      # REST endpoints (/, /hello, /health)
│   │   └── resources/
│   │       └── application.properties    # Base application configuration
│   └── test/
│       └── java/com/example/demo/
│           └── HelloControllerTest.java  # Automated JUnit 5 Unit Tests (CI Enhancement)
├── Jenkinsfile                           # Declarative Jenkins CI Pipeline
├── .github/workflows/ci.yml              # GitHub Actions CI workflow (Bonus Enhancement)
├── Dockerfile                            # Multi-Stage Dockerfile (Build + Distroless/JRE runtime)
├── ansible/                              # Component 2: Ansible Configuration Management
│   ├── inventory.ini                     # Host inventory with variables
│   ├── playbook.yml                      # Main playbook (vars, handlers, role inclusion)
│   ├── roles/
│   │   └── common/tasks/main.yml         # Reusable baseline setup role
│   └── templates/
│       └── devops-demo.service.j2        # Systemd unit Jinja2 template
├── k8s/                                  # Component 2: Kubernetes Deployment & Scaling
│   ├── configmap.yaml                    # ConfigMap for externalized settings (Enhancement)
│   ├── deployment.yaml                   # K8s Deployment with 3 replicas & Liveness/Readiness Probes
│   └── service.yaml                      # K8s NodePort Service (Port 30080)
└── report/
    └── report.tex                        # Complete LaTeX report formatted to MSRIT guidelines
```

---

## Component 1: CI/CD Pipeline (Git, Maven, Jenkins)

### 1. Git Workflow Execution
```bash
cd devops-demo
git init
git checkout -b feature/hello-endpoint
git add .
git commit -m "feat: implement Spring Boot REST endpoints and test suite"
git checkout -b main
git merge feature/hello-endpoint
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/devops-demo.git
git push -u origin main
```

### 2. Local Build & Test Verification
```bash
# Run automated tests
mvn test

# Package JAR artifact
mvn clean package -DskipTests
```

### 3. Running Jenkins via Docker
If you do not have Jenkins installed locally:
```bash
docker run -d \
  -p 8080:8080 -p 50000:50000 \
  --name jenkins \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```
1. Open `http://localhost:8080` in your browser.
2. Get the initial admin password: `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword`.
3. Install suggested plugins (includes Git, Pipeline, JUnit).
4. Create a **New Item** -> **Pipeline** -> named `devops-demo-pipeline`.
5. Under **Build Triggers**, select **Poll SCM** (`* * * * *`).
6. Under **Pipeline**, select **Pipeline script from SCM**, choose **Git**, and provide your GitHub repository URL and branch `main`.
7. Click **Save** and trigger a build (**Build Now**).

---

## Component 2: Ansible & Kubernetes Deployment

### 1. Ansible Execution
To test the Ansible playbook on your local system or target Linux machine:
```bash
cd ansible
ansible-playbook -i inventory.ini playbook.yml --check   # Dry run
ansible-playbook -i inventory.ini playbook.yml           # Execution
```

### 2. Docker Containerization
```bash
# Build multi-stage image
docker build -t devops-demo:1.0 .

# Verify image size & security
docker images | grep devops-demo
```

### 3. Kubernetes Local Cluster Deployment (Minikube / Kind)
```bash
# Start Minikube
minikube start

# Load image into minikube's Docker daemon
minikube image load devops-demo:1.0
# (Alternatively: eval $(minikube docker-env) && docker build -t devops-demo:1.0 .)

# Apply Kubernetes manifests
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Verify running pods
kubectl get pods -l app=devops-demo
kubectl get svc devops-demo-service
```

### 4. Horizontal Scaling Demonstration
```bash
# Scale up from 3 to 5 pods
kubectl scale deployment devops-demo --replicas=5

# Inspect scaled pods
kubectl get pods -l app=devops-demo -o wide

# Test service endpoint
minikube service devops-demo-service --url
curl $(minikube service devops-demo-service --url)/
curl $(minikube service devops-demo-service --url)/health
```

---

## Screenshots to Capture for Your Report

| Sl No | Component | What to Screenshot | Command / Page |
|---|---|---|---|
| 1 | Comp 1 | Git branch and commit graph | `git log --graph --oneline --all` |
| 2 | Comp 1 | Jenkins Pipeline Execution View | Stage View showing Checkout -> Tests -> Build -> Archive (Green checks) |
| 3 | Comp 1 | Test Results in Jenkins | JUnit test result page showing passed tests |
| 4 | Comp 1 | Archived Artifact | Jenkins build page showing `devops-demo.jar` under Last Successful Artifacts |
| 5 | Comp 2 | Ansible Playbook Run | Terminal output of `ansible-playbook` with `ok` / `changed` statuses |
| 6 | Comp 2 | Kubernetes Initial Pods (3 replicas) | `kubectl get pods -l app=devops-demo` |
| 7 | Comp 2 | Kubernetes Scaling (5 replicas) | `kubectl get pods -l app=devops-demo -o wide` |
| 8 | Comp 2 | Liveness & Readiness Probes & ConfigMap | `kubectl describe pod <pod-name>` |
| 9 | Comp 2 | Application Live Response | Browser / curl output showing greeting from ConfigMap |

---

## Viva & Evaluation Q&A Cheat Sheet (Concept Understanding)

1. **Why use Webhooks instead of SCM Polling?**
   - *Answer:* SCM Polling is pull-based where Jenkins periodically queries Git on a timer (wasting CPU cycles and introducing latency). Webhooks are push-based; GitHub immediately fires an HTTP POST event to Jenkins when a commit arrives, triggering builds with zero delay.
2. **What is an Ansible Handler and why is it used?**
   - *Answer:* A handler is a conditional task that runs at the end of a play only when notified by another task that reported a `changed` status. It prevents redundant service restarts when configuration files have not been modified, preserving idempotency.
3. **What is the difference between a Pod, a ReplicaSet, and a Deployment?**
   - *Answer:* A **Pod** is the smallest deployable unit containing one or more containers. A **ReplicaSet** ensures a specified number of identical pods are running at all times. A **Deployment** is a higher-level abstraction that manages ReplicaSets, enabling declarative updates, rolling zero-downtime upgrades, and rollbacks.
4. **Why use a ConfigMap instead of baking settings into the Docker image?**
   - *Answer:* Decouples configuration from application code (12-Factor App methodology). The same immutable container image can be promoted from dev to staging to prod simply by binding different ConfigMaps, avoiding rebuilds.
5. **What is the difference between Liveness and Readiness probes?**
   - *Answer:* A **Readiness probe** determines if a container is ready to accept user traffic (if it fails, the service stops routing requests to that pod). A **Liveness probe** determines if the container is healthy/alive (if it fails, the kubelet kills and restarts the container).
