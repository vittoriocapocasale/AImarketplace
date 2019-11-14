FROM openjdk:8
RUN mkdir /usr/src/AIapp
COPY ./AIapp.jar /usr/src/AIapp
WORKDIR /usr/src/AIapp
EXPOSE 8080
CMD ["java", "-jar", "AIapp.jar"]
