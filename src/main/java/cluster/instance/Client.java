package cluster.instance;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Client implements Watcher {
    ZooKeeper zooKeeper;
    String hostPort;

    Client(String hostPort) {
        this.hostPort = hostPort;
    }

    void startZooKeeper(int sessionTimeOut) throws IOException {
        zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
    }

    public void stopZooKeeper() throws InterruptedException {
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
