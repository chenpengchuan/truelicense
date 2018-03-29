#!/usr/bin/env bash

set -e
cd ${WORK_DIR}
mkdir -p temp
echo "workdir:" `pwd`

start() {
   echo "cmd-line:"
    echo "         ${APP_CMD}"
    echo "pid-file:" ${PID_FILE}
    ${APP_CMD} > /dev/null 2>&1&
    echo $! > ${PID_FILE}
   echo "${APP_NAME} has beed started"
}

stop() {
    echo "try to stop ${TRUELICENSE_COM_NAME}"
    if [ -f "${PID_FILE}" ];
    then
        cat ${PID_FILE} | xargs kill
        echo "killing ${APP_NAME}"
        sleep 3
        processId=$(cat ${PID_FILE})
        ps -fe|grep ${processId} |grep -v grep
        if [ $? -eq 0 ]
         then
         cat ${PID_FILE} | xargs kill -9
        fi
        rm ${PID_FILE}
        echo "${APP_NAME} has been killed"
    else
      echo "${APP_NAME} has been stopped"
    fi
}

action="${1}"
case ${action} in
    'start')
        start
        ;;
    'stop')
        stop
        ;;
    'restart')
        stop
        start
        ;;
    *)
        echo 'e.g: env WORK_DIR="${WORK_DIR}" APP_NAME="${TRUELICENSE_COM_NAME}" PID_FILE="${PID_FILE}" ${TRUELICENSE_BIN}/truelicense-launcher-helper.sh start|stop|restart|reload'
        ;;
esac