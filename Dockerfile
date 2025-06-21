# -------- BUILD STAGE --------
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /app

# Кэшируем зависимости
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle --no-daemon build || true

# Копируем всё и собираем
COPY . .
RUN gradle installDist --no-daemon

# -------- RUNTIME STAGE --------
FROM openjdk:17-jdk-slim
WORKDIR /app

# Копируем билд из предыдущего stage
COPY --from=build /app/build/install/server /app

# Railway передаёт порт через переменную PORT
ENV PORT=8080
EXPOSE ${PORT}

CMD ["./bin/server"]