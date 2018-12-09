#!/usr/bin/bash

echo "=================== Building a package ==================="
mvn package

sudo cp /home/dmitry/IdeaProjects/RESTServer/target/RESTServer-1.0-SNAPSHOT.war /var/lib/tomcat/webapps

echo "=================== Starting Tomcat ==================="
sudo tomcat start

echo "..."
sleep 10s

echo "=================== Running a test query ==================="
curl "http://localhost:8080/RESTServer-1.0-SNAPSHOT/trade"
