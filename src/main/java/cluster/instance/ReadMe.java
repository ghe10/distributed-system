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
 */
public class ReadMe {

}
