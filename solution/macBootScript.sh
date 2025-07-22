#!/bin/bash
# This script opens three terminal tabs and runs npm start in the first two
# and ./gradlew bootRun in the third, using the current directory.
# Make sure to run this script from the root of your project directory.
# Usage: ./bootScript.sh
# Ensure the script is executable
chmod +x bootScript.sh
# Check if the script is run from the project root
if [ ! -d "central-server" ] || [ ! -d "dynamic-data-server" ] || [ ! -d "static-data-server" ]; then
    echo "Please run this script from the root of your project directory."
    exit 1
fi

PROJECT_ROOT="$(pwd)"

osascript <<EOF
tell application "Terminal"
    activate
    do script "cd \"$PROJECT_ROOT/central-server\" && npm start"
    delay 1
    do script "cd \"$PROJECT_ROOT/dynamic-data-server\" && npm start"
    delay 1
    do script "cd \"$PROJECT_ROOT/static-data-server\" && ./gradlew bootRun"
end tell
EOF