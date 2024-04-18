#!/bin/zsh
docker rm -f $(docker ps -a -q)     ## comment out to prevent old docker image removal

cd ./dataspawner
mvn clean package
docker build -t camunda/data-spawner .

cd ..
docker-compose -f ./Docker-compose.yaml up
