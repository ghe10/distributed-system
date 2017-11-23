#!/bin/bash

echo "**** update server code ****"

cd /home/vm1/ghe10/Servers/Server1/Distributed-system
git pull
echo "1" > "/home/vm1/ghe10/Servers/Server1/Distributed-system/myid/myid"
echo "tickTime=2000
initLimit=5  
syncLimit=2
dataDir=/home/vm1/ghe10/Servers/Server1/Distributed-system/myid
clientPort=2181
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
" > "/home/vm1/ghe10/Servers/Server1/Distributed-system/config/configuration.cfg"

cd /home/vm1/ghe10/Servers/Server2/Distributed-system
git pull
echo "2" > "/home/vm1/ghe10/Servers/Server2/Distributed-system/myid/myid"
echo "tickTime=2000
initLimit=5  
syncLimit=2
dataDir=/home/vm1/ghe10/Servers/Server2/Distributed-system/myid
clientPort=2181
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
" > "/home/vm1/ghe10/Servers/Server2/Distributed-system/config/configuration.cfg"

cd /home/vm1/ghe10/Servers/Server3/Distributed-system
git pull
echo "3" > "/home/vm1/ghe10/Servers/Server3/Distributed-system/myid/myid"
echo "tickTime=2000
initLimit=5  
syncLimit=2
dataDir=/home/vm1/ghe10/Servers/Server3/Distributed-system/myid
clientPort=2181
server.1=127.0.0.1:2888:3888
server.2=127.0.0.1:2889:3889
server.3=127.0.0.1:2890:3890
" > "/home/vm1/ghe10/Servers/Server3/Distributed-system/config/configuration.cfg"

echo "****  update finished   ****"
