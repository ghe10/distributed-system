package cluster;

import org.apache.zookeeper.KeeperException;
import utils.ObservableList;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Scanner;

/**
 * This class provides main method for the cluster part. The purpose of this class is test
 */
public class ClusterUserClient {
    private static final String HOST_PORT = "3888";
    private static final int SESSION_TIMEOUT = -1;
    private static final String QUIT = "quit";
    private static final String GET_MASTER = "get-master";
    private static final String GET_NODES = "get-nodes";

    private static void commandHandler(ClusterNodeWrapper nodeWrapper) {
        Scanner scanner = new Scanner(System.in);
        String command;
        while (true) {
            System.out.println("Please input command");
            command = scanner.nextLine();
            if (command.equals(QUIT)) {
                break;
            } else if (command.equals(GET_MASTER)) {
                System.out.println(String.format("Master ip : %s", nodeWrapper.getMasterIp()));
            } else if (command.equals(GET_NODES)) {
                System.out.println("Cluster node ip information :");
                HashSet<String> nodeIps = nodeWrapper.getNodeIps();
                int count = 0;
                System.out.println("");
                for (String ip : nodeIps) {
                    System.out.println(String.format("  Node %s: %s", Integer.toString(count), ip));
                }
                System.out.println("");
            } else {
                System.out.println("Invalid input !");
            }
        }
    }

    public static void main(String args[]) {
        LinkedList<String> linkedList = new LinkedList<String>();
        ObservableList<String> observableList = new ObservableList<String>(linkedList);
        Observer observer = new BasicObserver();
        try {
            ClusterNodeWrapper nodeWrapper =
                    new ClusterNodeWrapper(observableList, observer, HOST_PORT, SESSION_TIMEOUT);
            System.out.println("******** This cluster node is started ! *********");
            // test operations goes here
            commandHandler(nodeWrapper);
            System.out.println("******** This cluster node is closed ! *********");
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } catch (KeeperException exception) {
            exception.printStackTrace();
        }
    }
}
