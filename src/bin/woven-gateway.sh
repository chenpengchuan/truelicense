#!/usr/bin/env bash

set -e

bin=`dirname "${BASH_SOURCE-$0}"`
bin=`cd "$bin"; pwd`

action="$1"

GATEWAY_BIN=${GATEWAY_BIN:-${bin}}
GATEWAY_HOME=`cd $(dirname $0)/..; pwd`
GATEWAY_CONF=${GATEWAY_CONF:-${GATEWAY_HOME}/conf}

GATEWAY_COM_NAME=woven-gateway
. ${GATEWAY_CONF}/woven-gateway-env.sh
. ${GATEWAY_BIN}/gateway-process-helper.sh

checkConfig
checkAction "${action}" "${GATEWAY_COM_NAME}.sh"

PID_FILE="${GATEWAY_COM_NAME}.pid"
JVM_OPTS="${JVM_OPTS} ${WOVEN_GATEWAY_JVM_OPTS}"
JVM_CLASSPATH="${GATEWAY_CONF}/:${GATEWAY_HOME}/${GATEWAY_COM_NAME}/lib/*"

for jardir in ${WOVEN_GATEWAY_EXTRA_LIBS//,// };
do
    JVM_CLASSPATH="${JVM_CLASSPATH}:${jardir}/*"
done

JVM_OPTS="${JVM_OPTS} -DGATEWAY_COM_NAME=${GATEWAY_COM_NAME} -DGATEWAY_LOGS_DIR=${GATEWAY_LOGS_DIR} -classpath ${JVM_CLASSPATH} com.merce.woven.gateway.GatewayApplication"

APP_CMD="${JAVA_CMD} ${JVM_OPTS}"
WORK_DIR="${GATEWAY_HOME}/${GATEWAY_COM_NAME}"

env WORK_DIR="${WORK_DIR}" APP_NAME="${GATEWAY_COM_NAME}" APP_CMD="${APP_CMD}" PID_FILE="${PID_FILE}"  ${GATEWAY_BIN}/gateway-launcher-helper.sh $@
