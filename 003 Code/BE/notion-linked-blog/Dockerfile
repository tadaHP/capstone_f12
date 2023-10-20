FROM openjdk:17-jdk-alpine
LABEL authors="gusvy"
ENV SPRING_PROFILES_ACTIVE=prod
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080