FROM eclipse-temurin:25-jdk

RUN apt-get update && \
    apt-get install -y nginx openssh-server

WORKDIR /app

COPY build/libs/*.jar app.jar
COPY start.sh /start.sh

RUN chmod +x /start.sh

EXPOSE 8080
EXPOSE 8443
EXPOSE 22

CMD ["/start.sh"]