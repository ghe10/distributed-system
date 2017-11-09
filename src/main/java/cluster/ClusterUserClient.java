package cluster;

import utils.ObservableList;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Observer;

/**
 * This class provides main method for the cluster part. The purpose of this class is test
 */
public class ClusterUserClient {
    private static final String HOST_PORT = "3888";
    private static final int SESSION_TIMEOUT = -1;

    public static void main(String args[]) {
        LinkedList<String> linkedList = new LinkedList<String>();
        ObservableList<String> observableList = new ObservableList<String>(linkedList);
        Observer observer = new BasicObserver();
        try {
            ClusterNodeWrapper nodeWrapper =
                    new ClusterNodeWrapper(observableList, observer, HOST_PORT, SESSION_TIMEOUT);
            // test operations goes here
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
