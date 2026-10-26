FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/cth-trade-finance-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms4g -Xmx8g"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
