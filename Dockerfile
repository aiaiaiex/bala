# GRADLE
FROM gradle:8.14.2-jdk11 AS GRADLE
WORKDIR /app
COPY . .
RUN gradle clean
RUN gradle fatJar

# JAVA
FROM adoptopenjdk/openjdk11:jre-11.0.27_6
ENV JAR_NAME=bala.jar
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=GRADLE $APP_HOME .
ENTRYPOINT exec java -jar $APP_HOME/app/build/libs/$JAR_NAME  