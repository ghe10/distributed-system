package cluster.instance;

/**
 * In this package, all the classes extends the Watcher interface in zooKeeper. This interface will help us connect the
 * current node to zooKeeper. The connection will create a background thread to maintain the zooKeeper session. The
 * watcher event will be triggered when sth happen to the znode.
 *
 * File system strategy: send file first, then message
 * Communication strategy: Main replica tell client done. I think this is better
 */
public class ReadMe {

}
