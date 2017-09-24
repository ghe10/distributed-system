package cluster.instance;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Client extends BasicWatcher {

    public Client(String hostPort, int sessionTimeOut) {
        super(hostPort, sessionTimeOut);
    }

    public boolean initClient() {
        try {
            startZooKeeper();
            return true;
        } catch(InterruptedException exception) {
            return false;
        } catch(IOException exception) {
            return false;
        }
    }

    public boolean stopClient() {
        try {
            stopZooKeeper();
            return true;
        } catch (InterruptedException exception) {
            return false;
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
