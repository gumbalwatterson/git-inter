FROM openjdk:21
ADD target/*.jar git-integration.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "git-integration.jar"]