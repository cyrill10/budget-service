FROM eclipse-temurin:17-jre
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8090
COPY target/*.jar app.jar
EXPOSE 8080 8090
ENTRYPOINT ["java","-jar","/app.jar"]
