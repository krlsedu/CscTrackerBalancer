
FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app

ARG JAR_FILE=target/csctracker-balancer.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Dfile.encoding=UTF-8 -XX:+UseSerialGC","-jar","app.jar"]
