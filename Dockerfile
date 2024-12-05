# Base image
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/naval_battle-1.0-SNAPSHOT.jar /app

EXPOSE 6433

ENTRYPOINT ["java","-jar","naval_battle-1.0-SNAPSHOT.jar"]