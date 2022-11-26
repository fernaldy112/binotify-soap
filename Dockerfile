FROM eclipse-temurin:latest

WORKDIR /usr/src/binotify-soap
COPY ./target/binotify-soap-1.0-SNAPSHOT.jar binotify-soap.jar
COPY .env .env
EXPOSE 80

ENTRYPOINT ["java", "-jar", "binotify-soap.jar"]
