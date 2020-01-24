#!/usr/bin/env sh

FOLDER=$(dirname "$0")
CONFIG_PATH=""$FOLDER/application.yml""
APP_PATH="$FOLDER/kiosk.jar"

echo "FOLDER $FOLDER"
echo "CONFIG_PATH is $CONFIG_PATH"
echo "APP_PATH is $APP_PATH"

java "-Dmicronaut.config.files=$CONFIG_PATH" --enable-preview -jar "$APP_PATH"
