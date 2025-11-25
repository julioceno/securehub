FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .

RUN ./mvnw package

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
