FROM jhipster/jhipster:latest

WORKDIR /app
COPY *.jdl /app

RUN \
    npx jhipster jdl --no-insight application.jdl && \
    npx jhipster jdl --no-insight model.jdl

COPY seeddata/*.csv /app/src/main/resources/config/liquibase/fake-data/

EXPOSE 8080
CMD ./mvnw
