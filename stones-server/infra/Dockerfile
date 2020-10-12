FROM openjdk:14
ARG JAR_FILE=build/libs/server-all.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 3000
