FROM java:8  
MAINTAINER Nikos Triantafyllou
VOLUME /tmp
VOLUME /configEidas
ADD ./target/dipSupSign-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
EXPOSE 8090
