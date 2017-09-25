FROM openjdk:8u131-jre-alpine
RUN apk add --no-cache unzip
ENV VERSION 0.0.1
RUN mkdir -p /usr/src/serviceorder
ADD https://github.com/lucaslsl/serviceorder-api/releases/download/v0.0.1/serviceorder-0.0.1.jar.zip /usr/src/serviceorder/
WORKDIR /usr/src/serviceorder
RUN unzip serviceorder-0.0.1.jar.zip
RUN mv serviceorder-$VERSION.jar serviceorder.jar
EXPOSE 8080
CMD ["java", "-jar", "serviceorder.jar"]
