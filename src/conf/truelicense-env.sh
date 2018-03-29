#!/usr/bin/env bash

set -e
export JAVA_HOME=${JAVA_HOME}     #custom sample: export JAVA_HOME=${JAVA_HOME:-'/usr/jdk64/jdk1.8.0_77'}
export JAVA_CMD=${JAVA_HOME}/bin/java
export TRUELICENSE_HOME=`cd $(dirname $0)/..; pwd`
export TRUELICENSE_CONF=${TRUELICENSE_CONF:-${TRUELICENSE_HOME}/conf}
export TRUELICENSE_LOGS_DIR=${TRUELICENSE_HOME}/logs

## set extra jar dirs
#export TRUELICENSE_EXTRA_LIBS="/usr/hdp/current/hbase-client/lib,xxxx/jars"

TRUELICENSE_JVM_OPTS="-Xmx4g"