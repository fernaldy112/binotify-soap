FROM maven:3.8.6-openjdk-18

WORKDIR /usr/src/binotify-soap
COPY . .
RUN mvn package

EXPOSE 80
ENTRYPOINT ['java', '-jar', 'target/binotify-soap-1.0-SNAPSHOT.jar']
