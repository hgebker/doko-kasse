FROM eclipse-temurin:17-jdk-alpine
VOLUME /doko
COPY target/*.jar doko-kasse.jar
ENTRYPOINT ["java","-jar","/doko-kasse.jar"]