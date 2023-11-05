FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/socialhub_api-0.0.1-SNAPSHOT.jar app.jar


# Set environment variables to override Spring Boot properties
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysqldb:3306/social_hub
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=1234
ENV SPRING_JPA_GENERATE_DDL=true
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=none
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
ENV SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=5

ENTRYPOINT ["java","-jar","/app.jar"]