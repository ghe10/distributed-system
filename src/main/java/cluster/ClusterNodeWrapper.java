package cluster;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import utils.ObservableList;

import java.io.IOException;
import java.util.HashSet;
import java.util.Observer;

/**
 * This class servers as a wrapper of the cluster part. It also helps to
 * run the cluster part separately.
 */

public class ClusterNodeWrapper {
    private Node node;
    private ZooKeeper zooKeeper;

    public ClusterNodeWrapper(ObservableList<String> observableList, Observer observer, String hostPort,
                              int sessionTimeOut) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper(hostPort, sessionTimeOut, null);
        node = new Node(zooKeeper, observableList);
        node.initNode();
        MembershipHandler.setMembershipHandler(observableList, observer);
    }

    public String getMasterIp() {
        return node.getMasterIp();
    }

    public HashSet<String> getNodeIps() {
        return node.getNodeIps();
    }

    public boolean isMaster() {
        return node.isMaster();
    }

    public void shutDown() throws InterruptedException {
        zooKeeper.close();
    }
}
