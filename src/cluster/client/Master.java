package cluster.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Master implements Watcher{
    private ZooKeeper zooKeeper;
    private String hostPort;
    private int sessionTimeOut;

    Master(String hostPort) {
        this.hostPort = hostPort;
        sessionTimeOut = 15000;
    }

    Master(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
    }

    void startZooKeeper() throws IOException{
        if (!isStarted()) {
            zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
        }
    }

    public void process(WatchedEvent event) {

    }

    public boolean isStarted() {
        return zooKeeper != null;
    }
}
