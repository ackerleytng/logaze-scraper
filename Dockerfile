# syntax=docker/dockerfile:1

FROM clojure:lein AS builder
WORKDIR /tmp/
COPY . .
RUN lein ring uberjar

FROM gcr.io/distroless/java25
COPY --from=builder /tmp/target/logaze.jar /app/logaze.jar
WORKDIR /app
CMD ["logaze.jar"]
