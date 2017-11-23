#!/bin/bash

echo "**** start servers ****"
cd /home/vm1/ghe10/Servers/Server1/Distributed-system
mvn clean
mvn compile
#mvn exec:java@ZkServer

cd /home/vm1/ghe10/Servers/Server2/Distributed-system
mvn clean
mvn compile
mvn exec:java@ZkServer

cd /home/vm1/ghe10/Servers/Server3/Distributed-system
mvn clean
mvn compile
mvn exec:java@ZkServer

echo "**** start finished ****"
