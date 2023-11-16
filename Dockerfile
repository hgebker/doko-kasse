FROM amazoncorretto:17-alpine
VOLUME /doko
ENV AWS_ACCESS_KEY_ID=""
ENV AWS_SECRET_KEY=""
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]