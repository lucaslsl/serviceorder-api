FROM openjdk:8-jre-alpine
RUN mkdir /usr/src/serviceorder
COPY target/serviceorder-1.0-SNAPSHOT.jar /usr/src/serviceorder/
WORKDIR /usr/src/serviceorder
CMD ["java", "-jar", "serviceorder-1.0-SNAPSHOT.jar"]