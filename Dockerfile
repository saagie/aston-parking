FROM adoptopenjdk/openjdk8:alpine
MAINTAINER Saagie <pierre@saagie.com>

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar", "/usr/share/aston-parking.jar"]

# Add the service itself
ARG JAR_FILE
ADD ${JAR_FILE} /usr/share/aston-parking.jar
CMD ["java", "-jar", "/usr/share/croilsssants-kotlin.jar"]