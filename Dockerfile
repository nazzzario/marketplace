FROM maven:3.8.3-openjdk-17
WORKDIR /usr/local/backend
COPY . /usr/local/backend
RUN mvn clean package
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.profiles.active=dev", "-jar","/usr/local/backend/target/marketplace-0.0.1.jar"]
