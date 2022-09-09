FROM gcr.io/distroless/java17-debian11
COPY target/logaze.jar /app/logaze.jar
WORKDIR /app
CMD ["logaze.jar"]
