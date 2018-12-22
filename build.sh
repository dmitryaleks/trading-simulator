#!/usr/bin/bash

echo "=================== Building a package ==================="
rm -rf target
mvn package

sudo rm -rf /var/lib/tomcat/webapps/*
sudo cp /home/dmitry/IdeaProjects/RESTServer/target/RESTServer-1.0-SNAPSHOT.war /var/lib/tomcat/webapps

echo "=================== Starting Tomcat ==================="
sudo tomcat start

echo "..."
sleep 10s

echo "=================== Running a test query ==================="
curl "http://localhost:8080/RESTServer-1.0-SNAPSHOT/trade"
