package cluster;

import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.File;
import java.io.IOException;

/**
 * The server bootstrap functions are moved to Server.java. This file is no longer used in the system
 */

public class ZooKeeperServer {
    private QuorumPeerConfig zooKeeperServerConfig;
    private QuorumPeer server;
    private ServerCnxnFactory factory;

    public ZooKeeperServer(String configPath) throws QuorumPeerConfig.ConfigException, IOException {
        zooKeeperServerConfig = new QuorumPeerConfig();
        zooKeeperServerConfig.parse(configPath);
        System.out.println(String.format("Server config : %s",zooKeeperServerConfig.toString()));
        System.out.println("*************** config parse success ****************");
    }

    public void run() {
        try {
            File dataDir = new File(zooKeeperServerConfig.getDataDir());
            File dataLogDir = new File(zooKeeperServerConfig.getDataLogDir());
            FileTxnSnapLog fileTxnSnapLog = new FileTxnSnapLog(dataDir, dataLogDir);
            ZKDatabase zooKeeperDataBase = new ZKDatabase(fileTxnSnapLog);
            factory = new NIOServerCnxnFactory();
            server = new QuorumPeer(
                    zooKeeperServerConfig.getServers(),
                    dataDir,
                    dataLogDir,
                    zooKeeperServerConfig.getElectionAlg(),
                    zooKeeperServerConfig.getServerId(),
                    zooKeeperServerConfig.getTickTime(),
                    zooKeeperServerConfig.getInitLimit(),
                    zooKeeperServerConfig.getSyncLimit(),
                    zooKeeperServerConfig.getQuorumListenOnAllIPs(),
                    factory,
                    zooKeeperServerConfig.getQuorumVerifier()
            );
            server.setMinSessionTimeout(zooKeeperServerConfig.getMinSessionTimeout());
            server.setMaxSessionTimeout(zooKeeperServerConfig.getMaxSessionTimeout());
            server.setZKDatabase(zooKeeperDataBase);
            server.setLearnerType(zooKeeperServerConfig.getPeerType());

            server.start();
            server.join();

        } catch (InterruptedException exception) {

        } catch (IOException exception) {

        }
    }

    public boolean isRunning() {
        if (server == null) {
            return false;
        }
        return server.isRunning();
    }

    public QuorumPeerConfig getConfig() {
        return zooKeeperServerConfig;
    }

    public QuorumPeer getServer () {
        return server;
    }

    public boolean isLeader() {
        if (server == null) {
            return false;
        }
        return server.getState().equals(QuorumPeer.ServerState.LEADING);
    }

    public boolean shutDown() {
        if (server == null) {
            return true;
        }
        server.shutdown();
        return server.isRunning();
    }
}
