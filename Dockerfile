# server base image - java 17
FROM openjdk:17-alpine

# set the working directory
WORKDIR /app

# copy .jar file to docker
COPY ./build/libs/stuv-0.0.1-SNAPSHOT.jar stuv.jar

# expose the application port
EXPOSE 8080

# always do command
ENTRYPOINT ["java", "-jar", "stuv.jar"]