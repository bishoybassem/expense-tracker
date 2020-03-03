FROM openjdk:11-jre-slim

LABEL maintainer="bishoy"

RUN apt-get update \
    && apt-get install -y --no-install-recommends netcat curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ARG REPORTER_JAR
ARG JACOCO_AGENT_JAR
COPY ${REPORTER_JAR} ${JACOCO_AGENT_JAR} docker-entrypoint.sh /webapp/

EXPOSE 8080

HEALTHCHECK CMD curl -f http://localhost:8080/reporter/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=test

WORKDIR /webapp
ENTRYPOINT ["./docker-entrypoint.sh"]