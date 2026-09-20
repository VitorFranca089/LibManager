#!/usr/bin/env bash
set -e

export DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/libmanager}"
export DB_USERNAME="${DB_USERNAME:-root}"
export DB_PASSWORD="${DB_PASSWORD:-libmanager}"

cd "$(dirname "$0")"

mvn -q compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java \
  -Dexec.mainClass=io.github.vitorfranca089.libmanager.Main
