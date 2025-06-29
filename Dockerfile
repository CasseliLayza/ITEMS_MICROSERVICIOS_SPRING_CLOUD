FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/ITEMS_MICROSERVICIOS_SPRING_CLOUD-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} msvc_items.jar
EXPOSE 8002
CMD ["java", "-jar", "msvc_items.jar"]