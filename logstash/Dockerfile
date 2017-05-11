FROM docker.elastic.co/logstash/logstash:5.4.0

MAINTAINER Florian Besser

ADD logstash.conf /config-dir/logstash.conf
CMD ["logstash", "-f", "/config-dir/logstash.conf"]
