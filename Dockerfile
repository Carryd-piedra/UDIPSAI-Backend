# ETAPA 1: BUILD (Compilación y Creación del JAR)
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivo de configuración
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src src

# Empaquetar la aplicación
RUN mvn package -DskipTests -B

# ETAPA 2: RUN (Ejecución)
FROM eclipse-temurin:17-jre-focal AS runner

WORKDIR /app

# Exponer puerto
EXPOSE 8080

# Copiar el JAR generado
# Basado en pom.xml: <artifactId>UDIPSAI-Backend</artifactId> <version>1.0.0</version>
COPY --from=builder /app/target/UDIPSAI-Backend-1.0.0.jar app.jar

# Configuración de variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]