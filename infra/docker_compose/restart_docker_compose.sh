#!/bin/sh

echo ">>> Stop Docker Compose"
docker compose down

echo ">>> Pulling all browsers images"

CONFIG_FILE="./config/browsers.json"

# Use Docker to run jq
if ! command -v jq >/dev/null 2>&1; then
    echo "jq not found. Using Docker to run jq..."

    # Pull lightweight alpine with jq
    docker pull alpine/jq:latest >/dev/null 2>&1

    # Function to use dockerized jq
    jq() {
        docker run --rm -i -v "$(pwd):/work" -w /work alpine/jq "$@"
    }
    export -f jq
fi

# Now jq will work even if not installed locally
jq -r '.[].versions[].image' "$CONFIG_FILE" 2>/dev/null | while read -r image; do
    if [ -n "$image" ]; then
        echo ">>> Pulling $image"
        docker pull "$image"
    fi
done

echo ">>> Done!"
echo ">>> Starting Docker Compose"
docker compose up -d