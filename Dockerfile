# ───────────────────────── Dependencies ─────────────────────────
FROM gradle:8.5-jdk21 AS dependencies

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./

COPY gradle ./gradle

RUN gradle dependencies --no-daemon

# ───────────────────────── Build ─────────────────────────
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon

# ───────────────────────── Production ─────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN mkdir -p /app/database

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=production"]
