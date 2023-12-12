FROM jhipster/jhipster:v8.1.0

WORKDIR /app
COPY *.jdl /app

# Generate a git repo to make jhipster happy
RUN \
    git config --global user.email "you@example.com" && \
    git config --global user.name "Your Name" && \
    git init

# Generate the code
RUN \
    npx jhipster jdl --no-insight --force application.jdl && \
    npx jhipster jdl --no-insight --force model.jdl

# Copy the initial data
RUN \
    rm -rf /app/src/main/resources/config/liquibase/fake-data/car.csv && \
    rm -rf /app/src/main/resources/config/liquibase/fake-data/customer.csv && \
    rm -rf /app/src/main/resources/config/liquibase/fake-data/employee.csv && \
    rm -rf /app/src/main/resources/config/liquibase/fake-data/location.csv
COPY seeddata/*.csv /app/src/main/resources/config/liquibase/fake-data/

# Install all the packages
RUN ./mvnw package

EXPOSE 8080
CMD ./mvnw
