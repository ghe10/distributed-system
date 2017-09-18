package cluster.instance;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Worker implements Watcher {
    private ZooKeeper zooKeeper;
    private String hostPort;
    private String serverId;
    private String status;
    private String name;
    private int sessionTimeOut;

    public Worker(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
    }

    public Worker(String hostPort) {
        this.hostPort = hostPort;
        this.sessionTimeOut = 15000;
    }

    public Worker(String hostPort, String serverId, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
        this.serverId = serverId;
        this.name = String.format("worker-%s", serverId);
    }

    public void startZooKeeper() throws IOException, InterruptedException {
        if (!isStarted()) {
            zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
        }
    }

    public void stopZooKeeper() throws InterruptedException {
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

    public void setStatus(String status) {
        this.status = status;
        updateStatus(status);
    }

    private void register() {
        zooKeeper.create(String.format("/worker/worker-%s", serverId),
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createWorkerCallback,
                null);
    }

    private AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // in this case, we retry the request
                    register();
                    break;
                case OK:
                    System.out.println("*************** Parent created ****************");
                    break;
                case NODEEXISTS:
                    System.out.println("*************** Parent already registered ****************");
                     break;
                default:
                    System.out.println(String.format("Error: %s %s",
                            KeeperException.create(KeeperException.Code.get(rc)), path));
            }
        }
    };

    private AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch(KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    updateStatus((String)ctx);
                    return;
            }
        }
    };

    synchronized private void updateStatus(String status) {
        if (status.equals(this.status)) {
            zooKeeper.setData(String.format("/workers/%s", name), status.getBytes(), -1,
                                            statusUpdateCallback, status);
        }
    }
}
