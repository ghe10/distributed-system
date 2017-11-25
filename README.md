# Distributed-system
This is another implementation of the distributed system class project. ZooKeeper is used in the new implementation.
For now the file system is separated into two parts: the cluster membership part and the storage part. Append operation
will be allowed in this system. The current implementation uses some higher level Java features and packages including RMI
and observer. The storage nodes also work with RMI calls. 

Some notes: all the zk server ip and ports should be passed into zk in each node.

2017/11/25 Stop here

Note : this implementation doesn't satisfy the requirement of cs425, so don't do anything strange! If you are working on
cs 425, please code from scratch without using high level Java features.
