# syntax=docker/dockerfile:1

FROM clojure:lein-2.12.0-bullseye AS builder
WORKDIR /tmp/
COPY . .
RUN lein ring uberjar

FROM gcr.io/distroless/java21-debian12
COPY --from=builder /tmp/target/logaze.jar /app/logaze.jar
WORKDIR /app
CMD ["logaze.jar"]
