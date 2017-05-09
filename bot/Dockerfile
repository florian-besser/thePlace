FROM openjdk:8-jre

MAINTAINER Florian Besser

RUN apt-get update && apt-get install -y dnsutils

COPY build/distributions/bot.tar /home/bot.tar
RUN tar -xvf /home/bot.tar -C /home/
CMD ["/home/bot/bin/bot"]