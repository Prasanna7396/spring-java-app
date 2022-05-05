FROM maven:3-jdk-8

RUN \
 apt-get update &&\
 apt-get install -y bash curl 

USER root

RUN mkdir -p /opt/app

WORKDIR /opt/app

EXPOSE 8080

COPY . /opt/app

CMD ["mvn","jetty:run"] 
