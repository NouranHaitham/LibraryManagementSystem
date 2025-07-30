FROM openjdk:21-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

COPY out/artifacts/Library_app_jar/Library-app.jar app.jar
COPY wait-for-it.sh .

RUN chmod +x wait-for-it.sh

CMD ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]
