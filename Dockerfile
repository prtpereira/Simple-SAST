FROM maven:3.8.4-openjdk-17 as build
WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package


FROM openjdk:17 as release_img
WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/sast-checkmarx-1.0-SNAPSHOT.jar sast-checkmarx.jar

RUN mkdir input output
COPY input/* input/
COPY input_params.txt .

CMD ["java", "-jar", "sast-checkmarx.jar"]