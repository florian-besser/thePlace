FROM openjdk:8-jre

MAINTAINER Florian Besser

RUN apt-get update && apt-get install -y dnsutils net-tools netcat

COPY build/distributions/backend.tar /home/backend.tar
RUN tar -xvf /home/backend.tar -C /home/
CMD ["/home/backend/bin/backend"]