package cluster.server;

import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.apache.zookeeper.server.quorum.QuorumStats;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This file defines the zooKeeper server bootstrap file
 */

public class Server extends QuorumPeerMain implements Runnable {
    private QuorumPeerConfig zkConfig;

    public Server(String configPath) {
        zkConfig = new QuorumPeerConfig();
        try {
            zkConfig.parse(configPath);
        } catch (QuorumPeerConfig.ConfigException exception) {
            System.err.println("*************** server load config failed  ****************");
            System.err.println(String.format("%s", exception.getCause()));
            exception.printStackTrace();
        }
    }

    public void run() {
        try {
            runFromConfig(zkConfig);
        } catch (IOException exception) {
            System.err.println("*************** server bootstrap failed ****************");
            exception.printStackTrace();
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
        System.out.println(quorumPeer.getServerState());
        return quorumPeer.getServerState().equals(QuorumStats.Provider.LEADING_STATE);
    }

    public boolean shutDown() {
        if (quorumPeer == null) {
            return true;
        }
        quorumPeer.shutdown();
        return quorumPeer.isRunning();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File folder = new File(".");
        String input = "";
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        Server server = new Server("config/configuration.cfg");
        Thread t = new Thread(server);
        t.start();

        while (!input.equals("q")) {
            input = scanner.next();
            System.out.println("******************** status *******************");
            boolean leaderStatus = server.isLeader();
            System.out.println(String.format("Isleader : %s", leaderStatus));
            System.out.println(String.format("Isrunning : %s", server.isRunning()));
        }
        server.shutDown();
        System.exit(-1);
    }
}
