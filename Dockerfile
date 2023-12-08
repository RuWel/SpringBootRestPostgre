FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Rudi Welter"

# CREATE WORKING DIR
WORKDIR /myapp

ARG jar=SpringBootRestPostgres-0.0.1-SNAPSHOT

# COPY FILES FROM HOST (our project) TO WORKDIR (because the path is relative)
COPY mvnw .
COPY pom.xml .

# COPY .mvn folder
WORKDIR /myapp/.mvn
COPY .mvn .

# COPY sources
WORKDIR /myapp/src
COPY src .

WORKDIR /myapp
# build application using maven
RUN ./mvnw clean install -DskipTests
# copy target jar tho WORKDIR
RUN cp target/${jar}.jar app.jar
# remove target folder
RUN ["rm", "-rf", "target"]

# execute jar
ENTRYPOINT ["java","-jar","app.jar"]
