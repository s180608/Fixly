# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src/ src/
# The developer's application.properties is excluded from the build context.
COPY docker/application.properties src/main/resources/application.properties
RUN --mount=type=cache,target=/root/.m2 ./mvnw -B -DskipTests package

# Run the existing test suite against the separate test database in Compose.
FROM build AS test
CMD ["./mvnw", "-B", "test"]

FROM eclipse-temurin:17-jre-jammy AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 fixly \
    && useradd --uid 10001 --gid fixly --no-create-home fixly
WORKDIR /app
COPY --from=build --chown=fixly:fixly /workspace/target/fixly-backend-0.0.1-SNAPSHOT.jar app.jar
USER fixly
ENV TZ=Europe/London
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=60s --retries=10 \
    CMD curl --fail --silent http://localhost:8080/api/services > /dev/null || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
