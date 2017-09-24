package cluster.instance;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * TO avoid init zooKeeper object every where, we add this class as base class
 */

public class BasicWatcher implements Watcher {
    protected ZooKeeper zooKeeper;
    protected String hostPort;
    protected int sessionTimeOut;

    public BasicWatcher(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut != 0 ? sessionTimeOut : 15000;
    }


    protected void startZooKeeper() throws IOException, InterruptedException {
        if (!isStarted()) {
            zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
        }
    }

    protected void stopZooKeeper() throws InterruptedException {
        if (zooKeeper != null) {
            zooKeeper.close();
        }
    }

    public void process(WatchedEvent event) {
        // to be implemented
    }

    public boolean isStarted() {
        return zooKeeper != null;
    }
}
