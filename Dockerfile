# ---------- BUILD ----------
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app

COPY . .

RUN ./gradlew :server:installDist --stacktrace --no-daemon

# ---------- RUNTIME ----------
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/server/build/install/server /app

# 👇 вот это добавь
RUN mkdir -p /app/storage

ENV STORAGE_PATH=/app/storage
ENV PORT=8080
EXPOSE 8080

CMD ["./bin/server"]