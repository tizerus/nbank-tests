FROM maven:3.9.11-eclipse-temurin-21-alpine

# Default args values
ARG TEST_PROFILE=api
ARG BASE_API_URL=http://host.docker.internal:4111
ARG BASE_UI_URL=http://host.docker.internal:3000
ARG REMOTE_URL=http://host.docker.internal:4444/wd/hub
ARG BROWSER=chrome
ARG BROWSER_SIZE=1920x1080

# Values for container
ENV TEST_PROFILE=${TEST_PROFILE}
ENV BASE_API_URL=${BASE_API_URL}
ENV BASE_UI_URL=${BASE_UI_URL}
ENV REMOTE=${REMOTE_URL}
ENV BROWSER=${BROWSER}
ENV BROWSER_SIZE=${BROWSER_SIZE}

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .

CMD /bin/sh -c " \
    mkdir -p /app/logs ; \
    echo '=========================================' ; \
    echo 'Test Execution Environment:' ; \
    echo '  Profile: ${TEST_PROFILE}' ; \
    echo '  Base API URL: ${BASE_API_URL}' ; \
    echo '  Base UI URL: ${BASE_UI_URL}' ; \
    echo '  Remote URL: ${REMOTE}' ; \
    echo '  Browser: ${BROWSER}' ; \
    echo '  Browser Size: ${BROWSER_SIZE}' ; \
    echo '=========================================' ; \
    echo '>>>> Running tests...' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    echo '>>>> Generating report...' ; \
    mvn -DskipTests=true surefire-report:report \
    2>&1 | tee /app/logs/run.log ; \
    echo '=========================================' ; \
    echo 'Tests finished!' ; \
    echo '========================================='"