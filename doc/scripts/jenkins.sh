#!/bin/bash
# description: 用于jenkins 项目部署脚本（本机）
# author: jackson rick

# 防止nohup进程被kill
BUILD_ID=dontKillMe
echo "run script from build number: ${BUILD_NUMBER}"

JAVAPATH="/opt/jdk1.8/bin"
# 基于JENKINS_HOME/workspace/项目名
BUILDPATH="myapp/target/myapp-1.0"
# 部署目标路径
APPATH="/data/apps/myapp"
# jar包名称
JAR_NAME="myapp-1.0.jar"

PID=$(ps -aux | grep ${JAR_NAME} | grep -v grep | awk '{print $2}' )
if [ -n "$PID" ];then
    kill -9 $PID
fi

# 备份旧文件，会先删除上次备份，这里仅更新主jar包和lib目录，可根据需要修改
BACK_PATH=$APPATH/backup
if [ ! -d "$BACK_PATH" ];then
  mkdir $BACK_PATH
fi
rm -rf $BACK_PATH/*
mv $APPATH/$JAR_NAME $BACK_PATH
mv $APPATH/lib $BACK_PATH
# 更新文件
mv -f $BUILDPATH/$JAR_NAME $APPATH
mv -f $BUILDPATH/lib $APPATH

# 启动脚本
nohup $JAVAPATH/java -Xmx2G -jar $APPATH/$JAR_NAME >/dev/null 2>&1 &
echo -e "$JAR_NAME is started."