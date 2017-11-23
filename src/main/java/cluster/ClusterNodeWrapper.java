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
    private ZkNode node;
    private ZooKeeper zooKeeper;

    public ClusterNodeWrapper(ObservableList<String> observableList, Observer observer, String hostInfo,
                              int sessionTimeOut) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper(hostInfo, sessionTimeOut, null);
        node = new ZkNode(zooKeeper, observableList);
        node.init();
        MembershipHandler.setMembershipHandler(observableList, observer);
    }

    public String getMasterIp() {
        return node.getMasterIp();
    }

    public HashSet<String> getNodeIps() {
        try {
            return node.getNodeIps();
        } catch (KeeperException exception) {
            return null;
        } catch (InterruptedException exception) {
            return null;
        }
    }

    public boolean isMaster() {
        return node.isMaster();
    }

    public void shutDown() throws InterruptedException {
        zooKeeper.close();
    }
}
