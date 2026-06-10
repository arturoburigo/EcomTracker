# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY . /app

# Lint runs in CI and in the pre-commit hook; the Docker build only packages.
RUN mvn clean package -DskipTests -Dspotless.check.skip=true -Dcheckstyle.skip=true

# Runtime stage
FROM openjdk:8-oracle
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
