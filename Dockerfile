FROM jboss/wildfly:10.1.0.Final
COPY target/shortener.war /opt/jboss/wildfly/standalone/deployments/shortener.war