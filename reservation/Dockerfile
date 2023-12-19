FROM eclipse-temurin:17-jdk-jammy
WORKDIR /opt
ENV PORT 8070
EXPOSE 8070
COPY target/*.jar /opt/reservation.jar
ENTRYPOINT exec java $JAVA_OPTS -jar reservation.jar