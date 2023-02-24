
# Build stage

FROM maven:3.9.1-jdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage

FROM openjdk:17-jdk-slim
COPY --from=build /target/vky-app.jar vky-app.jar
# ENV PORT=8080
EXPOSE 9696
ENTRYPOINT ["java","-jar","vky-app.jar"]
