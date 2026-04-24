#!/bin/sh
set -e  # Останавливаться при любой ошибке

IMAGE_NAME=nbank_tests
TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +%d%m%Y_%H%M)
TEST_OUTPUT=./test_output/$TIMESTAMP

docker build -t "$IMAGE_NAME" .

mkdir -p "$TEST_OUTPUT/logs" "$TEST_OUTPUT/results" "$TEST_OUTPUT/report"

docker run --rm \
  -v "$TEST_OUTPUT/logs":/app/logs \
  -v "$TEST_OUTPUT/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e BASE_API_URL=http://host.docker.internal:4111 \
  -e BASE_UI_URL=http://host.docker.internal:3000 \
  "$IMAGE_NAME"

echo "Tests completed. Results saved in:"
echo "  - Logs: $CURRENT_DIR/logs"
echo "  - Results: $CURRENT_DIR/results"
echo "  - Report: $CURRENT_DIR/report"