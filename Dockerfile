# Use Maven to build the project
FROM maven:3.9-eclipse-temurin-22-alpine AS build
WORKDIR /app

# Copy the entire project directory to /app
COPY . /app

# Package the application
RUN mvn clean package -DskipTests

# Use OpenJDK for the runtime environment
FROM openjdk:8-oracle
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
