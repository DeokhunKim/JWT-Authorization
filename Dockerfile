FROM openjdk:11.0.10-jre-slim-buster
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} jwt-authorizaion.jar
ENTRYPOINT ["java","-jar","/jwt-authorizaion.jar"]