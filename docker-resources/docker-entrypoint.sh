#!/bin/bash

set -e
set -x

${SPARK_HOME}/spark-submit \
    --class ${APP_CLASS} \
    --master spark://master:7077  \
    ${APP_BASE}/top-tracks-app.jar