FROM openjdk:22-jdk-slim


WORKDIR /app

COPY . /app

RUN chmod +x /app/gradlew

RUN ./gradlew jar

EXPOSE 5000

CMD ["java", "-jar", "build/libs/SignalServer.jar"]