FROM openjdk:17-jdk-slim
EXPOSE 8080
ADD target/vky-app.jar vky-app.jar
ENTRYPOINT ["java","-jar","/vky-app.jar"]


