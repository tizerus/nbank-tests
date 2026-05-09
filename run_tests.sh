#!/bin/sh
set -e

# Имя образа и контейнера
IMAGE_NAME=nbank_tests
TIMESTAMP=$(date +%d%m%Y_%H%M)
TEST_OUTPUT=./test_output/$TIMESTAMP
CONTAINER_NAME=nbank_tests_$TIMESTAMP

# Функция показа помощи
show_help() {
    cat << EOF
Usage: ./run_tests.sh [OPTIONS]

Options:
    -p, --profile PROFILE      Test profile (api, ui) [default: api]
    -e, --env KEY=VALUE        Set environment variable (can be used multiple times)
    --build-arg KEY=VALUE      Set build argument (can be used multiple times)
    --base-api-url URL         Base API URL [default: http://host.docker.internal:4111]
    --base-ui-url URL          Base UI URL [default: http://host.docker.internal:3000]
    --remote-url URL           Selenoid remote URL [default: http://host.docker.internal:4444/wd/hub]
    --browser BROWSER          Browser name [default: chrome]
    --browser-size SIZE        Browser size [default: 1920x1080]
    --headless true/false      Headless mode [default: false]
    --network NETWORK          Docker network [default: nbank-network]
    --help                     Show this help

Examples:
    # Run API tests
    ./run_tests.sh

    # Run UI tests
    ./run_tests.sh --profile ui

    # Run UI tests with custom URLs for Docker environment
    ./run_tests.sh --profile ui \\
        --base-ui-url http://frontend:80 \\
        --remote-url http://selenoid:4444/wd/hub \\
        --network nbank-network

    # Run tests with custom environment variables
    ./run_tests.sh --profile ui \\
        -e BROWSER=firefox \\
        -e ADMIN_NAME=custom_admin

    # Run tests with build arguments
    ./run_tests.sh --profile ui \\
        --build-arg JAVA_OPTS="-Xmx2g" \\
        --build-arg MAVEN_OPTS="-DskipTests=false"

    # Local development (tests on host machine)
    ./run_tests.sh --profile ui \\
        --base-ui-url http://localhost:3000 \\
        --remote-url http://localhost:4444/wd/hub \\
        --network host
EOF
    exit 0
}

# Значения по умолчанию
TEST_PROFILE="api"
BASE_API_URL="http://host.docker.internal:4111"
BASE_UI_URL="http://host.docker.internal:3000"
REMOTE_URL="http://host.docker.internal:4444/wd/hub"
BROWSER="chrome"
BROWSER_SIZE="1920x1080"
DOCKER_NETWORK="nbank-network"
ENV_VARS=""
BUILD_ARGS=""

# Парсинг аргументов
while [ $# -gt 0 ]; do
    case $1 in
        -p|--profile)
            TEST_PROFILE="$2"
            shift 2
            ;;
        -e|--env)
            ENV_VARS="$ENV_VARS -e $2"
            shift 2
            ;;
        --build-arg)
            BUILD_ARGS="$BUILD_ARGS --build-arg $2"
            shift 2
            ;;
        --base-api-url)
            BASE_API_URL="$2"
            ENV_VARS="$ENV_VARS -e BASE_API_URL=$2"
            shift 2
            ;;
        --base-ui-url)
            BASE_UI_URL="$2"
            ENV_VARS="$ENV_VARS -e BASE_UI_URL=$2"
            shift 2
            ;;
        --remote-url)
            REMOTE_URL="$2"
            ENV_VARS="$ENV_VARS -e REMOTE=$2"
            shift 2
            ;;
        --browser)
            BROWSER="$2"
            ENV_VARS="$ENV_VARS -e BROWSER=$2"
            shift 2
            ;;
        --browser-size)
            BROWSER_SIZE="$2"
            ENV_VARS="$ENV_VARS -e BROWSER_SIZE=$2"
            shift 2
            ;;
        --network)
            DOCKER_NETWORK="$2"
            shift 2
            ;;
        --help)
            show_help
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            ;;
    esac
done

# Вывод настроек
echo "========================================="
echo "Running tests with configuration:"
echo "  Profile: $TEST_PROFILE"
echo "  Network: $DOCKER_NETWORK"
echo "  Base API URL: $BASE_API_URL"
echo "  Base UI URL: $BASE_UI_URL"
echo "  Remote URL: $REMOTE_URL"
echo "  Browser: $BROWSER"
echo "  Browser Size: $BROWSER_SIZE"
echo "========================================="

# Очистка и сборка
echo "Building Docker image..."
docker build \
    --build-arg TEST_PROFILE="$TEST_PROFILE" \
    --build-arg BASE_API_URL="$BASE_API_URL" \
    --build-arg BASE_UI_URL="$BASE_UI_URL" \
    --build-arg REMOTE_URL="$REMOTE_URL" \
    --build-arg BROWSER="$BROWSER" \
    --build-arg BROWSER_SIZE="$BROWSER_SIZE" \
    $BUILD_ARGS \
    -t "$IMAGE_NAME" .

# Создаем директории для результатов
mkdir -p "$TEST_OUTPUT/logs" "$TEST_OUTPUT/results" "$TEST_OUTPUT/report"
chmod 777 "$TEST_OUTPUT/logs" "$TEST_OUTPUT/results" "$TEST_OUTPUT/report"

# Запускаем контейнер
# Если сеть host, не указываем --network, иначе используем указанную сеть
NETWORK_OPT=""
if [ "$DOCKER_NETWORK" != "host" ]; then
    NETWORK_OPT="--network $DOCKER_NETWORK"
fi

echo "Running tests in container..."
docker run \
    --name "$CONTAINER_NAME" \
    $NETWORK_OPT \
    -e TEST_PROFILE="$TEST_PROFILE" \
    $ENV_VARS \
    "$IMAGE_NAME"

# Копируем результаты
echo "Copying test results from container..."
docker cp "$CONTAINER_NAME:/app/logs/." "$TEST_OUTPUT/logs/" 2>/dev/null || echo "No logs found"
docker cp "$CONTAINER_NAME:/app/target/surefire-reports/." "$TEST_OUTPUT/results/" 2>/dev/null || echo "No surefire reports found"
docker cp "$CONTAINER_NAME:/app/target/site/." "$TEST_OUTPUT/report/" 2>/dev/null || echo "No site reports found"

# Удаляем контейнер
docker rm "$CONTAINER_NAME" > /dev/null 2>&1

# Показываем результат
echo ""
echo "========================================="
echo "Tests completed! Results saved in:"
echo "  📁 Logs: $TEST_OUTPUT/logs"
echo "  📁 Results: $TEST_OUTPUT/results"
echo "  📁 Report: $TEST_OUTPUT/report"
echo "========================================="

# Открываем отчет если есть
if [ -f "$TEST_OUTPUT/report/surefire-report.html" ]; then
    echo "📊 Report URL: file://$(pwd)/$TEST_OUTPUT/report/surefire-report.html"
fi