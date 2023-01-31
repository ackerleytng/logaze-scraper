# syntax=docker/dockerfile:1

FROM clojure:temurin-17-lein-2.10.0-bullseye AS builder
WORKDIR /tmp/
COPY . .
RUN lein ring uberjar

FROM gcr.io/distroless/java17-debian11
COPY --from=builder /tmp/target/logaze.jar /app/logaze.jar
WORKDIR /app
CMD ["logaze.jar"]
