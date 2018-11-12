#!/usr/bin/env bash

set -e

bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin"; pwd`

action="$1"

TRUELICENSE_BIN=${TRUELICENSE_BIN:-${bin}}
TRUELICENSE_HOME=`cd $(dirname $0)/..; pwd`
TRUELICENSE_CONF=${TRUELICENSE_CONF:-${TRUELICENSE_HOME}/conf}

TRUELICENSE_COM_NAME=truelicense
. ${TRUELICENSE_CONF}/truelicense-env.sh
. ${TRUELICENSE_BIN}/truelicense-process-helper

checkConfig
checkAction "${action}" "${TRUELICENSE_COM_NAME}.sh"

PID_FILE="${TRUELICENSE_COM_NAME}.pid"
JVM_OPTS="${JVM_OPTS} ${TRUELICENSE_JVM_OPTS}"
JVM_CLASSPATH="${TRUELICENSE_CONF}/:${TRUELICENSE_HOME}/${TRUELICENSE_COM_NAME}/lib/*"

for jardir in ${TRUELICENSE_EXTRA_LIBS//,// };
do
    JVM_CLASSPATH="${JVM_CLASSPATH}:${jardir}/*"
done

JVM_OPTS="${JVM_OPTS} -DTRUELICENSE_COM_NAME=${TRUELICENSE_COM_NAME} -DTRUELICENSE_LOGS_DIR=${TRUELICENSE_LOGS_DIR} -classpath ${JVM_CLASSPATH} de.schlichtherle.app.App"

APP_CMD="${JAVA_CMD} ${JVM_OPTS}"
WORK_DIR="${TRUELICENSE_HOME}/${TRUELICENSE_COM_NAME}"

env WORK_DIR="${WORK_DIR}" APP_NAME="${TRUELICENSE_COM_NAME}" APP_CMD="${APP_CMD}" PID_FILE="${PID_FILE}"  ${TRUELICENSE_BIN}/truelicense-launcher-helper.sh $@
