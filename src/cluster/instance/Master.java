package cluster.instance;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.AsyncCallback.StringCallback;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Master implements Watcher{
    private ZooKeeper zooKeeper;
    private String hostPort;
    private int sessionTimeOut;
    private boolean isLeader;

    String serverId;

    Master(String hostPort) {
        this.hostPort = hostPort;
        sessionTimeOut = 15000;

        Random random = new Random();
        serverId = Long.toString(random.nextLong());
    }

    Master(String hostPort, int sessionTimeOut) {
        this.hostPort = hostPort;
        this.sessionTimeOut = sessionTimeOut;
    }

    public void bootstrap() {
        // this function will create a bunch of folders in zookeeper for coordination
        createParent("/workers", new byte[0]);
        createParent("/assign", new byte[0]);
        createParent("tasks", new byte[0]);
        createParent("/status", new byte[0]);
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

    public boolean existMaster() {
        while (true) {
            try {
                Stat stat = new Stat();
                byte data[] = zooKeeper.getData("/master", false, stat);
                isLeader = new String(data).equals(serverId);
                return true;
            } catch(InterruptedException exception) {
                return false;
            } catch(KeeperException exception) {
                // do nothing
            }
        }
    }

    public void runForMaster() throws InterruptedException {
        while (true) {
            try {
                zooKeeper.create("/master", serverId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException exception) {
                isLeader = false;
                break;
            } catch (KeeperException exception) {

            }
            if (existMaster()) {
                break;
            }
        }
    }

    private void createParent(String path, byte[] data) {
        zooKeeper.create(path, data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createParentCallback,
                data);
    }

    private AsyncCallback.StringCallback createParentCallback = new StringCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // in this case, we retry the request
                    createParent(path, (byte[]) ctx);
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
