FROM sebp/elk

RUN rm /etc/logstash/conf.d/02-beats-input.conf
RUN rm /etc/logstash/conf.d/30-output.conf

COPY 01-input.conf /etc/logstash/conf.d/01-input.conf
COPY 30-output.conf /etc/logstash/conf.d/30-output.conf