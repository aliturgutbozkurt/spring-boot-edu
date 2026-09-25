# Exercise 1 — this image works, but it is big, runs as root and rebuilds everything on every change.
# TODO 1a: a multi-stage build — extract the jar into layers (java -Djarmode=tools ... extract --layers)
#          and copy the layers into the final image one by one, the least changing first
# TODO 1b: the final image contains a JRE, not a JDK — build one with jdeps + jlink (lesson section 3.2)
# TODO 1c: run as a user that is not root
# TODO 1d: java must be the main process (PID 1), so that "docker stop" reaches it
FROM amazoncorretto:27-alpine
COPY target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT java -jar /app.jar
