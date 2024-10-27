# Use the official Maven image with JDK 17 to create a build artifact
FROM maven:3.8.1-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

# Use the official OpenJDK 17 image to run the application
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/techassessmentWebfluxService-0.0.1-SNAPSHOT.jar /usr/local/lib/techassessmentWebfluxService.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/usr/local/lib/tech-assessment-service.jar"]
