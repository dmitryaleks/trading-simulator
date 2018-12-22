#!/usr/bin/bash

echo "Switching Database instance..."

if [ "$1" = "LOCAL" ]; then
    cp src/main/resources/database.properties.local src/main/resources/database.properties
elif [ "$1" = "CLOUD" ]; then
    cp src/main/resources/database.properties.cloud src/main/resources/database.properties
else
    echo "Unknown option: $1. Please specify LOCAL or CLOUD"
    exit 42
fi

echo "Done"
exit 0
