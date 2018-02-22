FROM openjdk:8-jre-alpine
VOLUME /tmp
WORKDIR /webapp
ARG TRACKER_JAR
ADD ${TRACKER_JAR} /webapp
EXPOSE 8080
ENTRYPOINT java -jar /webapp/tracker.jar