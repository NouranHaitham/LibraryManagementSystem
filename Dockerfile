FROM openjdk:21-jdk-slim

WORKDIR /app

# Install netcat
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*



# Copy the built jar from IntelliJ's artifact output
COPY out/artifacts/Library_Management_Console_App_jar/library-app.jar app.jar

# Copy the wait-for-it script
COPY wait-for-it.sh wait-for-it.sh


RUN chmod +x wait-for-it.sh

# Run the app only after DB is available
CMD ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]

