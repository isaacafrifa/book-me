FROM openjdk:17-slim AS base
# Set working directory within the container
WORKDIR /app
COPY . .
# Install dependencies
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Define a new image stage for the final application
FROM base AS final
# Copy the application jar file
COPY target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
