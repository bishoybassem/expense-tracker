FROM openjdk:8-jre-alpine
WORKDIR /webapp
ARG REPORTER_JAR
ADD ${REPORTER_JAR} /webapp
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT java -jar /webapp/reporter.jar