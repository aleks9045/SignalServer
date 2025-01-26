FROM openjdk:22-jdk-slim AS builder

WORKDIR /src

COPY . /src

RUN chmod +x /src/gradlew

RUN ./gradlew installDist

FROM openjdk:22-jdk-slim

COPY --from=builder /src/build/install/SignalServer ./src/SignalServer

EXPOSE 5000

CMD ["./src/SignalServer/bin/SignalServer"]