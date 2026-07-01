FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .


# 2. Fase de ejecución (Ejecuta el .jar con Java 21)
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copiamos el .jar recién compilado de la fase anterior
COPY --from=builder /app/target/usuarios-0.0.1-SNAPSHOT.jar app.jar

# Cambiado al puerto correcto que usa tu microservicio
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]