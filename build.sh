#!/usr/bin/env bash

SCRIPT_PATH=`readlink -f "$0"`
SCRIPT_DIR=`dirname "$SCRIPT_PATH"`

cd "$SCRIPT_DIR"
mvn clean package
java -jar target/geoimageviewer-1.0-SNAPSHOT.jar -d images
