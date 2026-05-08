# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

ARG MODULE

COPY pom.xml .
COPY commerce/pom.xml commerce/pom.xml
COPY commerce/interaction-api/pom.xml commerce/interaction-api/pom.xml
COPY commerce/shopping-store/pom.xml commerce/shopping-store/pom.xml
COPY commerce/shopping-cart/pom.xml commerce/shopping-cart/pom.xml
COPY commerce/warehouse/pom.xml commerce/warehouse/pom.xml
COPY commerce/order/pom.xml commerce/order/pom.xml
COPY commerce/payment/pom.xml commerce/payment/pom.xml
COPY commerce/delivery/pom.xml commerce/delivery/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY config-server/pom.xml config-server/pom.xml
COPY eureka-server/pom.xml eureka-server/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn -B -pl ${MODULE} -am -DskipTests dependency:go-offline

COPY commerce commerce
COPY api-gateway api-gateway
COPY config-server config-server
COPY eureka-server eureka-server

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl ${MODULE} -am -DskipTests package && \
    cp "$(find "${MODULE}/target" -maxdepth 1 -type f -name '*.jar' ! -name 'original-*' | head -n 1)" /tmp/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN useradd --create-home --shell /bin/bash spring

COPY --from=build /tmp/app.jar /app/app.jar
COPY --from=build /workspace/config-server/config-repo /app/config-repo

USER spring
EXPOSE 8080 8761 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
