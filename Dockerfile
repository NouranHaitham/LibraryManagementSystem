FROM openjdk:21-jdk-slim

WORKDIR /app

# Install netcat for wait-for-it.sh
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy app jar and wait script
COPY out/artifacts/Library_app_jar/Library-app.jar app.jar
COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh

CMD ["./wait-for-it.sh", "db:3306", "--", "sh", "-c", "env && java -jar app.jar"]
