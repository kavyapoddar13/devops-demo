# ==============================================================
# Multi-stage Dockerfile for DevOps Demo Application
# Stage 1: Build stage with Maven and JDK 17
# Stage 2: Minimal runtime stage with JRE 17 (Security & Size optimization)
# ==============================================================

# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root application user for cyber security best practices
RUN groupadd -r devops && useradd -r -g devops devops

COPY --from=builder /workspace/target/devops-demo.jar /app/devops-demo.jar

USER devops:devops
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/devops-demo.jar"]
