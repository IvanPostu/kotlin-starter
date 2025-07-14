FROM amazoncorretto:17.0.4

RUN mkdir /app
COPY app/build/libs/app-1.0-SNAPSHOT-all.jar /app/

CMD ["java", "-jar", "/app/app-1.0-SNAPSHOT-all.jar"]
