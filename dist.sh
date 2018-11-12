#!/bin/bash

echo 'starting dist......'
set -e
if [ -z "$TRUELICENSE_DIST" ]; then
	echo "[Usage]:" \
	     "env TRUELICENSE_DIST=truelicense-dist-path [SKIP_PACKAGE=1] ./dist.sh"
	echo "example:"
	echo "   env TRUELICENSE_DIST=/home/who/share/tmp/truelicense-dist  ./dist.sh"
	echo "   env TRUELICENSE_DIST=/home/who/share/tmp/truelicense-dist SKIP_PACKAGE=1 ./dist.sh"
	exit 1
fi

truelicense_project_base=$(pwd)
truelicense_project_version=$(mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q)


echo "prepare dirs"
if [ -z "${TRUELICENSE_DIST}" ]; then
    FULL_PACKAGE_NAME="truelicense-${truelicense_project_version}-$(date +%Y-%m-%d_%H-%M)"
fi

truelicense_dist_base="${TRUELICENSE_DIST}/${FULL_PACKAGE_NAME}"
echo 'truelicense_dist_base_base: '$truelicense_dist_base_base
mkdir -p ${truelicense_dist_base_base}
truelicense_dist_base_base=$(cd ${truelicense_dist_base_base} && pwd)
cd ${truelicense_dist_base_base}

mkdir conf bin logs docs
mkdir -p libs/jars
echo "${FULL_PACKAGE_NAME}" > conf/version

echo "prepare components dirs"
components=(truelicense)
project_groups=("truelicense")
components_length=${#components[@]}
components_idx_end=$((components_length-1))

cd ${truelicense_dist_base_base}
for i in $(seq 0 ${components_idx_end});
do
    scom=${components[i]}
    mkdir ${scom}
    mkdir logs/${scom}
    mkdir libs/${scom}
    ln -f -s ../libs/${scom} ${scom}/lib
    ln -f -s ../conf ${scom}/conf
    ln -f -s ../logs/${scom} ${scom}/logs
done

if [ -z "$SKIP_PACKAGE" ]; then
    cd ${truelicense_project_base}
    MVN_OPTS="-DskipTests=true"
    mvn clean package install ${MVN_OPTS}
fi

echo "copy all jars to libs/jars"
cd ${truelicense_project_base}
for i in $(seq 0 ${components_idx_end});
do
    projects=(${project_groups[i]})
    for p in ${projects[@]};
    do
      echo "copy jars from dep of $p (duplicate jars will be skipped)"
      pjdep="${truelicense_dist_base_base}/${p}.deps"
     # cd ${truelicense_project_base}/${p}

      case ${p} in
		"license-ui")
			echo "copy files for license ui"
			;;
		*)
			echo "computing dep of $p ..."
			scope_opt="-DincludeScope=runtime"
			mvn dependency:build-classpath -DskipTests=true ${scope_opt} -Dmdep.outputFile="${pjdep}" -q
			com_jars=$(cat ${pjdep})
			rm ${pjdep}
			if [ -f target/${p}-${truelicense_project_version}-pg.jar -a -f target/${p}-${truelicense_project_version}.jar ];then
                    rm -rf target/${p}-${truelicense_project_version}.jar
                    mv target/${p}-${truelicense_project_version}-pg.jar target/${p}-${truelicense_project_version}.jar
            fi
			com_jars="${com_jars//:/ } target/${p}-${truelicense_project_version}.jar"
			cp -n -f -v ${com_jars} ${truelicense_dist_base_base}/libs/jars
			com_jars_array=(${com_jars})

			for sjar in ${com_jars_array[@]};
			do
				sjar=$(basename ${sjar})
				ln -f -s -v ../../libs/jars/${sjar} ${truelicense_dist_base_base}/libs/${components[i]}
			done

			cd ${truelicense_dist_base_base}/libs/${components[i]}
			;;

      esac
    done
done

echo 'copy configuration files ......'
cd ${truelicense_project_base}
cp -R src/main/resources/* ${truelicense_dist_base_base}/conf
chmod +x ${truelicense_dist_base_base}/conf/*.sh

## shell scripts for startup & shutdown
cp  src/bin/* ${truelicense_dist_base_base}/bin
chmod +x ${truelicense_dist_base_base}/bin/*.sh

cd ${truelicense_project_base}