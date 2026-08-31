FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw \
  && chmod +x mvnw \
  && ./mvnw -B -ntp dependency:go-offline

COPY src ./src

RUN ./mvnw -B -ntp package

FROM eclipse-temurin:17-jre-jammy AS runtime

RUN groupadd --system fios \
  && useradd --system --gid fios --home-dir /app --shell /usr/sbin/nologin fios \
  && mkdir -p /app/uploads/images \
  && chown -R fios:fios /app

WORKDIR /app

COPY --from=build --chown=fios:fios /workspace/target/*.jar /app/app.jar

USER fios

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
