FROM openjdk:11-jre-slim
LABEL maintainer="bishoy"
RUN apt-get update \
    && apt-get install -y --no-install-recommends netcat=1.10-41.1 curl=7.64.0-4 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /webapp
ARG REPORTER_JAR
ARG JACOCO_AGENT_JAR
COPY ${REPORTER_JAR} /webapp
COPY ${JACOCO_AGENT_JAR} /webapp
COPY docker-entrypoint.sh /webapp
EXPOSE 8080
HEALTHCHECK CMD curl -f http://localhost:8080/reporter/actuator/health || exit 1
ENV SPRING_PROFILES_ACTIVE=test
ENTRYPOINT ["./docker-entrypoint.sh"]