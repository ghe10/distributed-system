package cluster.instance;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Client implements Watcher {
    private ZooKeeper zooKeeper;
    private String hostPort;
    private int sessionTimeOut;

    public Client(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
    }

    public boolean initClient() {
        try {
            zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
            return true;
        } catch (IOException exception) {
            // log the exception
            return false;
        }
    }

    public void stopClient() throws InterruptedException {
        if (zooKeeper != null) {
            zooKeeper.close();
        }
    }

    public String queueCommand(String command) throws KeeperException{
        while (true) {
            try {
                String name = zooKeeper.create("/task/task-", command.getBytes(),
                        OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                return name;
            } catch (InterruptedException exception) {
                System.err.println("*************** Interrupt exception in queueCommand ****************");
            }
        }
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
