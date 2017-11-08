package cluster;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import utils.StaticUtils;

import java.net.UnknownHostException;
import java.util.*;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Node {
    private ZooKeeper zooKeeper;
    private LinkedList<String> deadEventQueue;
    private HashSet<String> nodeIps;
    private String masterIp;
    private String serverId;
    private String nodeIp;
    private boolean isMaster;

    private static final String NODE_PATH = "/nodes";
    private static final String MASTER_PATH = "/master";

    public Node(ZooKeeper zooKeeper, LinkedList<String> deadEventQueue) throws UnknownHostException {
        this.zooKeeper = zooKeeper;
        this.deadEventQueue = deadEventQueue;
        nodeIps = new HashSet<String>();
        isMaster = false;
        Random random = new Random();
        serverId = String.valueOf(random.nextInt());
        nodeIp = StaticUtils.getLocalIp();
    }

    public void initNode() throws InterruptedException, KeeperException {
        // this function will register everything
        registerNode();
        if (!existMaster()) {
            runForMaster();
        }
        masterIp = getZkMasterIp();
        nodeIps = getZkNodeIps();
    }

    public String getMasterIp() {
        return masterIp;
    }

    public HashSet<String> getNodeIps() {
        return nodeIps;
    }

    public boolean isMaster() {
        return isMaster;
    }

    /** This is the start of worker operations*/
    private void registerNode() {
        zooKeeper.create(String.format("%s/worker-%s", NODE_PATH, serverId),
                nodeIp.getBytes(),
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
                    registerNode();
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
    /** This is then end of worker operations */

    /** This is the start of master operations */
    private boolean existMaster() {
        while (true) {
            try {
                Stat stat = new Stat();
                byte data[] = zooKeeper.getData(MASTER_PATH, false, stat);
                isMaster = new String(data).equals(serverId);
                return true;
            } catch(InterruptedException exception) {
                return false;
            } catch(KeeperException exception) {
                // do nothing
                return false;
            }
        }
    }

    private void masterRegistration() {
        while (true) {
            try {
                zooKeeper.create(MASTER_PATH, nodeIp.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isMaster = true;
                break;
            } catch (KeeperException.NodeExistsException exception) {
                isMaster = false;
                break;
            }  catch (InterruptedException exception) {
                isMaster = false;
                break;
            } catch (KeeperException.ConnectionLossException exception) {
                // do nothing, we should try again
            } catch (KeeperException exception) {
                isMaster = false;
                break;
            }
            if (existMaster()) {
                isMaster = false;
                break;
            }
        }
    }

    private void bootstrap() {
        // this function will create a bunch of folders in zookeeper for coordination
        createParent(NODE_PATH, new byte[0]);
    }

    private void createParent(String path, byte[] data) {
        zooKeeper.create(path, data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createParentCallback,
                data);
    }

    private AsyncCallback.StringCallback createParentCallback = new AsyncCallback.StringCallback() {
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

    private void runForMaster() {
        masterRegistration();
        if (isMaster) {
            // I am Master !!
            bootstrap();
        }
    }
    /** This is the end of master operations */

    private List<String> getZkNodes() {
        try {
            return zooKeeper.getChildren(NODE_PATH, false);
        } catch (InterruptedException exception) {
            return null;
        } catch (KeeperException exception) {
            return null;
        }
    }

    private HashSet<String> getZkNodeIps() {
        HashSet<String> newNodeIps = new HashSet<String>();
        List<String> nodeNames = getZkNodes();
        if (nodeNames == null) {
            return null;
        }
        try {
            for (String nodeName : nodeNames) {
                byte[] data = zooKeeper.getData(String.format("%s%s", NODE_PATH, nodeName), new NodeWatcher(), null);
                newNodeIps.add(new String(data));
            }
            return newNodeIps;
        } catch (InterruptedException exception) {
            return null;
        } catch (KeeperException exception) {
            return null;
        }
    }

    private String getZkMasterIp() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        byte masterData[] = zooKeeper.getData(MASTER_PATH, new MasterWatcher(), stat);
        return new String(masterData);
    }

    private class MasterWatcher implements Watcher {
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(MASTER_PATH)) {
                // TODO: runForMaster
                runForMaster();
                try {
                    masterIp = getZkMasterIp();
                } catch (InterruptedException exception) {

                } catch (KeeperException exception) {

                }
            }
        }
    }

    private class NodeWatcher implements Watcher {
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeChildrenChanged && event.getPath().equals(NODE_PATH)) {
                HashSet<String> newNodeIps = getZkNodeIps();
                if (newNodeIps == null) {
                    // TODO: take some action !
                    System.out.println("*********** Get node ips failed in watcher ************");
                } else {
                    // these strange locks are designed to avoid deadlock
                    // nodeIps can only be locked here
                    synchronized (deadEventQueue) {
                        synchronized (nodeIps) {
                            nodeIps.removeAll(newNodeIps);
                            for (String ip : nodeIps) {
                                deadEventQueue.add(ip);
                            }
                            nodeIps = newNodeIps;
                            deadEventQueue.notifyAll();
                        }
                    }
                }
            }
        }
    }
}
