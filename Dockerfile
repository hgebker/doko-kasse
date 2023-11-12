FROM arm32v7/openjdk:11-ea-17-jre-slim
VOLUME /doko
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]