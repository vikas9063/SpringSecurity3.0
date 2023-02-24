FROM openjdk:17
EXPOSE 8088
ADD target/vky-app.jar vky-app.jar
ENTRYPOINT ["java","-jar","/vky-app.jar"]
