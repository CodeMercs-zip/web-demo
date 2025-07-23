FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 17070
ENTRYPOINT ["java", "-jar", "app.jar"]
