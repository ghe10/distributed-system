package cluster;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import utils.ObservableList;
import utils.StaticUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ZkNode {
    private ZooKeeper zooKeeper;
    private String myIp;
    private ObservableList<String> deadEventQueue;
    private HashSet<String> currentNodes;
    private static String NODE_PATH = "/nodes";
    private static String MASTER_PATH = "/master";
    private static String MASTER_NAME = "master";

    public ZkNode(ZooKeeper zooKeeper, ObservableList<String> deadEventQueue) throws IOException {
        this.zooKeeper = zooKeeper;
        this.deadEventQueue = deadEventQueue;
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    private void create(String path, byte[] data, CreateMode mode) throws
            KeeperException, InterruptedException {
        zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
    }

    private Stat znodeExists(String path) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, true);
    }

    private void znodeSetWatcher(String path, Watcher watcher) throws KeeperException, InterruptedException {
        zooKeeper.getChildren(path, watcher);
    }

    private String getData(String path) throws KeeperException, InterruptedException {
        if (znodeExists(path) != null) {
            byte[] bytes = zooKeeper.getData(path, false, null);
            return new String(bytes);
        } else {
            return null;
        }
    }

    private List<String> getChildren(String path) throws KeeperException, InterruptedException {
        if (znodeExists(path) != null) {
            return  zooKeeper.getChildren(path, false);
        }
        return null;
    }

    public HashSet<String> getNodeIps() throws KeeperException, InterruptedException {
        HashSet<String> result = new HashSet<String>();
        List<String> nodes = getChildren(NODE_PATH);
        if (nodes == null) {
            return result;
        }
        for (String node : nodes) {
            String nodeIp = getData(String.format("%s/%s", NODE_PATH, node));
            if (nodeIp != null) {
                result.add(nodeIp);
            }
        }
        return result;
    }

    public void delete(String path) throws KeeperException, InterruptedException {
        System.out.println("delete op on version " + zooKeeper.exists(path, false).getVersion());
        zooKeeper.delete(path, zooKeeper.exists(path, false).getVersion());
    }

    public void init() throws KeeperException, IOException, InterruptedException {
        if (znodeExists(NODE_PATH) == null) {
            create(NODE_PATH, NODE_PATH.getBytes(), CreateMode.PERSISTENT);
        }
        if (znodeExists(MASTER_PATH) == null) {
            create(MASTER_PATH, MASTER_PATH.getBytes(), CreateMode.PERSISTENT);
        }
        myIp = StaticUtils.getLocalIp();
        if (znodeExists(String.format("%s/%s", MASTER_PATH, MASTER_NAME)) == null) {
            create(String.format("%s/%s", MASTER_PATH, MASTER_NAME), myIp.getBytes(), CreateMode.EPHEMERAL);
        }
        if (znodeExists(String.format("%s/%s", NODE_PATH, myIp)) == null) {
            create(String.format("%s/%s", NODE_PATH, myIp), myIp.getBytes(), CreateMode.EPHEMERAL);
        }
        // TODO : use real watcher to replace this watcher
        // master watcher should work on run for master
        MasterWatcher masterWatcher = new MasterWatcher();
        // node watcher should work on create new replicas
        NodeWatcher nodeWatcher = new NodeWatcher();
        znodeSetWatcher(MASTER_PATH, masterWatcher);
        znodeSetWatcher(NODE_PATH, nodeWatcher);
    }

    private class NodeWatcher implements Watcher {
        public void process(WatchedEvent event) {
            System.out.println("NodeWatcher activated!!*************" + event.getPath() + " " + event.getType());
            try {
                if (event.getType().equals(Event.EventType.NodeChildrenChanged)) {
                    HashSet<String> newNodes = getNodeIps();
                    currentNodes.removeAll(newNodes);
                    synchronized (deadEventQueue) {
                        for (String node : currentNodes) {
                            deadEventQueue.add(node);
                        }
                    }
                    currentNodes = newNodes;
                }
                znodeSetWatcher(NODE_PATH, new NodeWatcher());
            } catch (KeeperException exception) {
                exception.printStackTrace();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }

    }

    private class MasterWatcher implements Watcher {
        public void process(WatchedEvent event) {
            try {
                System.out.println("MasterWatcher activated!!*************" + event.getPath() + " " + event.getType());
                if (event.getType().equals(Event.EventType.NodeDeleted)) {
                    create(String.format("%s/%s", MASTER_PATH, MASTER_NAME), myIp.getBytes(), CreateMode.EPHEMERAL);
                    System.out.println("Master created");
                }
                znodeSetWatcher(String.format("%s/%s", MASTER_PATH, MASTER_NAME), new MasterWatcher());
            } catch (KeeperException exception) {
                exception.printStackTrace();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }


    public String getMasterIp() {
        try {
            return getData(String.format("%s/%s", MASTER_PATH, MASTER_NAME));
        } catch (KeeperException exception) {
            exception.printStackTrace();
            return null;
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean isMaster() {
        String masterIp = getMasterIp();
        if (masterIp.equals(myIp)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("192.168.56.101:2181", 1000, null);
        ZkNode node = new ZkNode(zk, new ObservableList<String>(new LinkedList<String>()));
        if (node.znodeExists(NODE_PATH) == null) {
            node.create(NODE_PATH, NODE_PATH.getBytes(), CreateMode.PERSISTENT);
        }
        node.init();
        System.out.println("Check create result " + node.getData(NODE_PATH));
        node.create(String.format("%s/%s", NODE_PATH, "myip0"),
                "ip0".getBytes(), CreateMode.EPHEMERAL);
        node.create(String.format("%s/%s", NODE_PATH, "myip1"),
                "ip1".getBytes(), CreateMode.EPHEMERAL);
        HashSet<String> nodes = node.getNodeIps();
        for (String ip : nodes) {
            System.out.println("    " + ip);
        }
        node.delete(String.format("%s/%s", NODE_PATH, "myip0"));
        System.out.println("delete " + String.format("%s/%s", NODE_PATH, "myip0"));
        node.delete(String.format("%s/%s", MASTER_PATH, MASTER_NAME));
        System.out.println("delete " + String.format("%s/%s", MASTER_PATH, MASTER_NAME));
        node.close();
    }
}
