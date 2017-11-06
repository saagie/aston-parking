FROM java:8-jre
MAINTAINER Saagie <pierre@saagie.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/aston-parking.jar"]

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/aston-parking.jar