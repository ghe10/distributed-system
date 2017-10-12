package cluster.instance;

/**
 * In this package, all the classes extends the Watcher interface in zooKeeper. This interface will help us connect the
 * current node to zooKeeper. The connection will create a background thread to maintain the zooKeeper session. The
 * watcher event will be triggered when sth happen to the znode.
 *
 * File system strategy: send file first, then message
 * Communication strategy: Main replica tell client done. I think this is better
 *
 * put strategy:
 * 1. client ask master for main replica ip
 * 2. master reply with main replica
 * 3. client send file to main replica
 * 4. client told main replica the new file
 * 5. main replica add two more replicas
 * 6. main replica told master about the add info (maybe also add info to a replicated file), save in mem first
 * 7. main replica told client
 *  we 'd better set a primary replica
 *
 * Update up on failure:
 * 1. each worker will keep checking the zooKeeper by watcher to determine the worker list change
 * 2. when some one dead:
 *      1. worker check those file, if : (1) I am main replica of a file (2) if yes and  the dead node contains info related to
 *         this file. Then : I am responsibility to add more replica and inform the other replicas about the change
 *         (3)if I am not main replica: remove the dead ip from this file's replication set, save the info struct to another
 *            queue that marked as wrong
 *
 *      2. master will check whether some primary replica died. it reassign new primary replica
 *
 *      3. if a worker is assigned as new primary replica: it will check whether we are short of replica. If yes, add one
 *
 * get strategy:
 * 1. client ask master for file
 * 2. master told replica to send it
 * 3. client file receiver should be waiting
 *
 * get between workers:
 * 1. worker ask master for file
 * 2. master told replica to send file
 * 3. we can use a while to check if we got the file
 * Here if we do some work, we may need suspend and resume.....
 *
 * get and delete happens in com
 *
 * 2017.10.11
 * Add ack for replication and add file
 * Change communication sequence: 1. send file 2. send info from file send queue 3. send info from comDataQueue
 * for all replica info
 * Actually we don't need step 2.
 *
 */
public class ReadMe {

}
