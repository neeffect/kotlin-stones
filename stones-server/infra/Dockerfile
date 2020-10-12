FROM c
ARG JAR_FILE=build/libs/*server.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

EXPOSE 3000
