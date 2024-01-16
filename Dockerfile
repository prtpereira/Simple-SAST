FROM maven:3.8.4-openjdk-17 as build
WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn package
# RUN mvn clean package -DskipTests


FROM openjdk:17-slim as release_img
WORKDIR /usr/src/app

# Copy the JAR file from the build stage
COPY --from=build /usr/src/app/target/sast-checkmarx-1.0-SNAPSHOT.jar sast-checkmarx.jar

# Create input and output directories
RUN mkdir input_src_files output_vulnerabilities
COPY input_src_files/* input_src_files/

CMD ["java", "-jar", "sast-checkmarx.jar"]