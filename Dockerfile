# APPLICATION BUILD
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

#ARG GITHUB_USERNAME
#ARG GITHUB_U

# Maven runner
COPY mvnw .
COPY .mvn .mvn

# Dependency
COPY pom.xml ./pom.xml
COPY ApplicantDiscoveryApi/pom.xml ./ApplicantDiscoveryApi/pom.xml
COPY ApplicantDiscoveryService/pom.xml ./ApplicantDiscoveryService/pom.xml

# Copy outside cache
COPY settings.xml /

RUN --mount=type=secret,id=GITHUB_USERNAME,env=GITHUB_USERNAME,required=true  \
    --mount=type=secret,id=GITHUB_KEY,env=GITHUB_KEY,required=true \
    --mount=type=cache,target=/root/.m2 \
    cp /settings.xml /root/.m2 && \
    cat /root/.m2/settings.xml && \
    ./mvnw dependency:go-offline -U

# Copy the full source code
COPY ApplicantDiscoveryApi/src ./ApplicantDiscoveryApi/src
COPY ApplicantDiscoveryService/src ./ApplicantDiscoveryService/src

# Build the Spring Boot application
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests

# Application Run
FROM eclipse-temurin:17-jdk AS runner

## Add a non-root user for security
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/ApplicantDiscoveryService/target/*.jar app.jar

# Expose the default Spring Boot port (you can override in compose)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]