# Etapa 1: Build
FROM gradle:8.9-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: Runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
ENV SPRING_PROFILES_ACTIVE=docker