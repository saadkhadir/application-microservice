# ======================
# Build stage
# ======================
FROM maven:3.9.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Build
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ======================
# Runtime stage
# ======================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier uniquement le jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]