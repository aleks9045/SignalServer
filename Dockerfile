FROM openjdk:22-jdk-slim


WORKDIR /app

COPY . /app

RUN ./gradlew build

EXPOSE 8000

CMD ["java", "-jar", "build/libs/SignalServer.jar"]