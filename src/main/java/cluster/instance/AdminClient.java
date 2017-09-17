package cluster.instance;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class AdminClient implements Watcher {
    private ZooKeeper zooKeeper;
    private String hostPort;
    private static final String MASTER_PATH = "/master";
    private static final String WORKER_PATH = "/workers";
    private static final String TASK_PATH = "/assign";
    private static final String WORKER_PATH_TEMPLATE = "/workers/%s";

    AdminClient(String hostPort) {
        this.hostPort = hostPort;
    }

    public void startZooKeeper(int sessionTimeOut) throws IOException {
        zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, this);
    }

    public void listState() {
        try {
            Stat stat = new Stat();
            byte masterData[] = zooKeeper.getData(MASTER_PATH, false, stat);
            System.out.println("*************** Master data ****************");
            System.out.println(new String(masterData));
            System.out.println("*************** Workers ****************");
            for (String worker : zooKeeper.getChildren(WORKER_PATH, false)) {
                byte data[] = zooKeeper.getData(String.format(WORKER_PATH_TEMPLATE, worker), false, null);
                System.out.println(String.format("\t%s: %s", worker, new String(data)));
            }
            System.out.println("*************** Tasks ****************");
            for (String task : zooKeeper.getChildren(TASK_PATH, false)) {
                System.out.println(String.format("\t%s", task));
            }
        } catch (KeeperException.NoNodeException exception) {
            System.out.println("Master doesn't exist !!");
        } catch (KeeperException | InterruptedException exception) {
            System.err.println(String.format("Exception in listState: %s ", exception));
        }
    }

    public void process(WatchedEvent event) {
        // sth to do here
    }
}
