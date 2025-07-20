#!/bin/bash

echo "开始打包项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
  echo "打包失败，退出脚本"
  exit 1
fi

JAR_FILE=$(ls target/*.jar | head -n 1)
if [ ! -f "$JAR_FILE" ]; then
  echo "找不到 jar 文件：$JAR_FILE"
  exit 1
fi


echo "启动 Spring Boot 应用"
java -jar "$JAR_FILE"
