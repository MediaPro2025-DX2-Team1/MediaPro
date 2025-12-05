#!/bin/bash

# MediaPro Component Preview Script
# Usage: ./preview.sh <ComponentName>
#        ./preview.sh list

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

if [ -z "$1" ]; then
    echo "Usage: ./preview.sh <ComponentName>"
    echo "       ./preview.sh list"
    echo ""
    echo "Run './preview.sh list' to see available components."
    exit 1
fi

cd "$SCRIPT_DIR"
./gradlew run --args="--preview $1" -q
