FROM jetty:10.0.7-jre11-slim
COPY target/playerdbweb.war /var/lib/jetty/webapps
COPY src/main/resources/logback.xml $JETTY_BASE/resources
ENTRYPOINT ["/bin/bash", "-c", "java -Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000 -jar $JETTY_HOME/start.jar", "--add-module=logging-logback", "--approve-all-licenses"]