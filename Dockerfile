FROM jetty:10.0.7
COPY target/playerdbweb.war /var/lib/jetty/webapps
ENV JETTY_HOME=/usr/local/jetty
ENTRYPOINT ["/bin/bash", "-c", "java -jar $JETTY_HOME/start.jar"]