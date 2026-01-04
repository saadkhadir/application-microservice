# ======================
# Build stage
# ======================
FROM maven:3.9.6-eclipse-temurin-17-slim AS builder

WORKDIR /app

# Optimisation du cache Maven
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Build
COPY src ./src
RUN mvn -B clean package -DskipTests

# ======================
# Runtime stage
# ======================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copier uniquement le jar
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
