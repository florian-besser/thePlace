FROM openjdk:8-jre
MAINTAINER Florian Besser
COPY build/distributions/bot.tar /home/bot.tar
RUN tar -xvf /home/bot.tar -C /home/
CMD ["/home/bot/bin/bot"]