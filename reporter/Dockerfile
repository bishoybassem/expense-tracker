FROM openjdk:11-jre-slim
LABEL maintainer="bishoy"
WORKDIR /webapp
ARG REPORTER_JAR
COPY ${REPORTER_JAR} /webapp
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT java -jar /webapp/reporter.jar