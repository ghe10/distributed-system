package cluster.server;

import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

import java.io.IOException;

/**
 * This file defines the zooKeeper server bootstrap file
 */

public class Server extends QuorumPeerMain implements Runnable {
    private int serverId = -1;
    private int nettyServerPort = -1;
    private String configPath = "";

    private QuorumPeerConfig zkConfig;

    public Server(int serverId, int nettyServerPort, String configPath) {
        this.serverId = serverId;
        this.nettyServerPort = nettyServerPort;
        this.configPath = configPath;

        zkConfig = new QuorumPeerConfig();
        try {
            zkConfig.parse(configPath);
        } catch (QuorumPeerConfig.ConfigException exception) {
            System.err.println("*************** server load config failed ****************");
        }
    }

    public void run() {
        try {
            runFromConfig(zkConfig);
        } catch (IOException exception) {
            System.err.println("*************** server bootstrap failed ****************");
        }
    }

    public boolean isRunning() {
        if (quorumPeer == null) {
            return false;
        }
        return quorumPeer.isRunning();
    }

    public QuorumPeerConfig getConfig() {
        return zkConfig;
    }

    public QuorumPeer getServer () {
        return quorumPeer;
    }

    public boolean isLeader() {
        if (quorumPeer == null) {
            return false;
        }
        return quorumPeer.getState().equals(QuorumPeer.ServerState.LEADING);
    }

    public boolean shutDown() {
        if (quorumPeer == null) {
            return true;
        }
        quorumPeer.shutdown();
        return quorumPeer.isRunning();
    }
}
