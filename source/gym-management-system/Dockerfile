FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw.cmd ./
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /app/target/gym-management-system-0.0.1-SNAPSHOT.jar app.jar

ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/gym_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true"
ENV SPRING_DATASOURCE_USERNAME="root"
ENV SPRING_DATASOURCE_PASSWORD="123456"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
