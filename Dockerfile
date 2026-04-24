FROM maven:3.9.11-eclipse-temurin-21-alpine
# Defaults args values
ARG TEST_PROFILE=api
ARG BASE_API_URL=http://host.docker.internal:4111
ARG BASE_UI_URL=http://host.docker.internal:3000
# Values for container
ENV TEST_PROFILE=${TEST_PROFILE}
ENV BASE_API_URL=${BASE_API_URL}
ENV BASE_UI_URL=${BASE_UI_URL}
# working from dir /app
WORKDIR /app
# Copy pom.xml file from project to current dir (/app), thats why we have dot as second arg
COPY pom.xml .
# upload dependency and cash it
RUN mvn dependency:go-offline
# Copy project to current dir, all project to current dir
COPY . .

USER root
# mvn test -P api (-P run profile api)
# mvn -DskipTests=true surfire-report:report
# write log to file, not in console
CMD /bin/sh -c " \
    mkdir -p /app/logs ; \
    echo '>>>> Running tests with profile: ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    echo '>>>> Running surefire report' ; \
    mvn -DskipTests=true surefire-report:report \
    2>&1 | tee /app/logs/run.log"