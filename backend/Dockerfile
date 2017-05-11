FROM openjdk:8-jre

MAINTAINER Florian Besser

RUN apt-get update && apt-get install -y dnsutils net-tools netcat

COPY build/distributions/backend.tar /home/backend.tar
RUN tar -xvf /home/backend.tar -C /home/

COPY build/frontend.zip /home/frontend.zip
RUN unzip -a /home/frontend.zip -d /home/frontend

CMD ["/home/backend/bin/backend"]