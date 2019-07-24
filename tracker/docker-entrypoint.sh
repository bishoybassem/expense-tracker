#!/bin/bash -e

for i in "$@"; do
    host_port=$(echo $i | tr ':' ' ')
    echo "Waiting for port to open: $host_port"
    while ! nc -zv $host_port; do
        sleep 5s
    done
done

exec java -javaagent:jacocoagent.jar=destfile=tracker.exec -jar tracker.jar