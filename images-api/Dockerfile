FROM gradle:8.10-alpine AS compile
COPY . .
RUN gradle clean build --no-daemon

FROM eclipse-temurin:21.0.4_7-jre-noble
WORKDIR /home/application/java

# Ensure the build/libs path is correct and the jar file is present
COPY --from=compile /home/gradle/build/libs/application.jar .

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/home/application/java/application.jar"]
