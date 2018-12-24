#!/bin/bash -xue

git checkout  ${TAG}||(echo "error,没有找到 ${TAG} 版本.";exit 1)
[  ! -z  ${APP_Build} ]||(echo "error,没有选择 APP_Build .";exit 1)

TAG=$(echo ${TAG}|sed 's/ *$//g'|sed 's/^ *//g')
echo "TAG号：【"${TAG}"】"

source /srv/ci-script/java_7_env.sh

#用于后台统计 =======>开始
UUID="$(cat /proc/sys/kernel/random/uuid)"
sh /srv/collect-script/collection_deploy_sum.sh uat1 ${JOB_NAME} ${TAG} ${APP_Build} ${BUILD_TAG} ${UUID} 
#用于后台统计 =======>结束

Deploy_app=$(echo ${APP_Build}|sed  -e 's/"//g;s/,/ /g;s/_build//g')

cd ${WORKSPACE}/p-trade-oc/p-trade-oc-api;
mvn -U clean install -P dev   -Dmaven.test.skip=true -am

for i in ${Deploy_app}; do
    cd ${WORKSPACE}/p-trade-oc/$i;
    mvn -U clean install -P dev   -Dmaven.test.skip=true -am
done

ENV_TMP=/var/tmp/jenkins_${JOB_NAME}_env.tmp
echo TAG=${TAG}>${ENV_TMP}
echo APP_Build=${APP_Build} >>${ENV_TMP}
#用于后台统计 =======>开始  #3.package end
sh /srv/collect-script/collection_deploy_detail.sh uat1 ${JOB_NAME} package end ${BUILD_TAG} ${UUID} 
echo UUID=${UUID} >>${ENV_TMP}
#用于后台统计 =======>结束
















#!/bin/bash -xue

# checkout git respo
git checkout  ${TAG} || (echo "error,没有找到 ${TAG} 版本.";exit 1)

TAG=$(echo ${TAG}|sed 's/ *$//g'|sed 's/^ *//g')
echo "TAG号：【"${TAG}"】"

# load java 环境变量
source /srv/ci-script/java_8_env.sh

# load nodejs 环境变量
export PATH=/data/apps/node-v6.5.0-linux-x64/bin/bin:$PATH

# load gradel 环境变量
export GRADLE_HOME=/data/apps/gradle-2.2.1/
PATH=$PATH:$GRADLE_HOME/bin



#用于后台统计 =======>开始
UUID="$(cat /proc/sys/kernel/random/uuid)"
sh /srv/collect-script/collection_deploy_sum.sh dev ${JOB_NAME} ${TAG} ${APP_Build} ${BUILD_TAG} ${UUID} 
#用于后台统计 =======>结束

## 打包
Deploy_app=$(echo ${APP_Build}|sed  -e 's/"//g;s/,/ /g;s/_build//g')
for i in ${Deploy_app}; do
    subd_tar_name=${i}-${TAG}
    full_tar_name=${i}#-${TAG}
    cd ${WORKSPACE}/$i
    mvn clean package -U -P dev -D maven.javadoc.skip=true -D skipTests=false -D maven.test.skip=true
done

# save parmas
ENV_TMP=/var/tmp/jenkins_${JOB_NAME}_env.tmp
echo TAG=${TAG}>${ENV_TMP}
echo APP_Build=${APP_Build} >>${ENV_TMP}

#用于后台统计 =======>开始  #3.package end
sh /srv/collect-script/collection_deploy_detail.sh dev ${JOB_NAME} package end ${BUILD_TAG} ${UUID} 
echo UUID=${UUID} >>${ENV_TMP}
#用于后台统计 =======>结束