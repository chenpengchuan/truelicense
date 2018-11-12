#!/usr/bin/env bash

function checkAction()
{
    actions=(start restart stop)
    if ! [[ " ${actions[*]} " == *" $1 "* ]]; then
       echo "Usage: $2 start|stop|restart"
       exit 1
    fi
}

function checkConfig()
{

    if [ ! -n "$JAVA_HOME" ]; then
        echo "JAVA_HOME must be set!"
        exit 1;
    fi

    APP_LOG_DIR=${TRUELICENSE_LOGS_DIR}/${TRUELICENSE_COM_NAME}
    mkdir -p ${APP_LOG_DIR}

    if [ "`readlink -f ${TRUELICENSE_LOGS_DIR}`" != "`readlink -f ${TRUELICENSE_HOME}/logs`" ]; then
        if [ "`readlink -f ${TRUELICENSE_HOME}`/logs" != `readlink -f ${TRUELICENSE_HOME}/logs` ]; then
            rm ${TRUELICENSE_HOME}/logs
        else
            mv "${TRUELICENSE_HOME}/logs" "${TRUELICENSE_HOME}/logs-back-$(date +%Y-%m-%d_%H-%M)"
        fi

        ln -s ${TRUELICENSE_LOGS_DIR} ${TRUELICENSE_HOME}/logs
    fi

}