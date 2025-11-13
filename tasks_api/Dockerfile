# Usar una imagen base de OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x ./mvnw

# Descargar dependencias (capa de caché)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación (sin ejecutar tests)
RUN ./mvnw clean package -DskipTests

# Etapa final - imagen más liviana
FROM eclipse-temurin:17-jre-alpine

# Crear directorio de aplicación
WORKDIR /app

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8091

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8091

# Configurar Java options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8091/api/tasks || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
