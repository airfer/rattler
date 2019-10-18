#!/usr/bin/env bash
# simulator travis build

mvn clean install

# run cobertura
mvn clean cobertura:cobertura

#upload
bash <(curl -s https://codecov.io/bash) -t 69a95c12-14d6-4663-aba6-e796c5fa7782