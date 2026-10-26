FROM amazoncorretto:21-alpine
VOLUME /doko
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Xss512k","-XX:+UseSerialGC","-jar","/app.jar"]
