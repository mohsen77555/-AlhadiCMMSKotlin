FROM gradle:8.13-jdk17 AS build
WORKDIR /workspace
COPY settings.gradle.kts build.gradle.kts ./
COPY src ./src
RUN gradle --no-daemon clean test installDist

FROM eclipse-temurin:17-jre
RUN useradd --system --create-home --uid 10001 appuser
WORKDIR /app
COPY --from=build /workspace/build/install/alhadi-cmms-backend/ /app/
USER appuser
EXPOSE 8080
ENTRYPOINT ["/app/bin/alhadi-cmms-backend"]
