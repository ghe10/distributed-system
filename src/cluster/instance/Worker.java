package cluster.instance;

import org.apache.zookeeper.*;

import java.io.IOException;

public class Worker implements Watcher {
    private ZooKeeper zooKeeper;
    private String hostPort;
    private String serverId;
    private int sessionTimeOut;

    public Worker(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
    }

    public Worker(String hostPort) {
        this.hostPort = hostPort;
        this.sessionTimeOut = 15000;
    }

    public Worker(String hostPort, String serverId) {
        this.hostPort = hostPort;
        this.serverId = serverId;
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

    private void register() {
        zooKeeper.create(String.format("/worker/worker-%s", serverId),
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createWorkerCallback,
                null);
    }

    private AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        @Override
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
}
