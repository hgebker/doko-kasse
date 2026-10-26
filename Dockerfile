FROM amazoncorretto:21-alpine
VOLUME /doko
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Xss512k","-XX:+UseSerialGC","-XX:MaxRAMPercentage=60","-XX:MaxMetaspaceSize=128m","-XX:ReservedCodeCacheSize=48m","-XX:TieredStopAtLevel=1","-Xshare:auto","-jar","/app.jar"]
