package filesystem;

import cluster.BasicObserver;
import cluster.ClusterNodeWrapper;
import filesystem.remoteclasses.RmiServer;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;
import org.apache.zookeeper.KeeperException;
import utils.FileSystemConstants;
import utils.ObservableList;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Scanner;

public class FileSystemWrapper {
    private FileSystemThreadPool fileSystemThreadPool;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;
    private RmiServer rmiServer;
    private Hashtable<String, FileStorageDataModel> storageInfo;
    private LinkedList<String> linkedList;
    private ObservableList<String> observableList;

    private static final String ZOOKEEPER_HOST = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT = 100000;
    private static final int CORE_POOL_SIZE = 20;
    private static final long KEEP_ALIVE_TIME = 100000L;

    public FileSystemWrapper() throws IOException, InterruptedException, KeeperException {
        storageInfo = new Hashtable<String, FileStorageDataModel>();
        scheduler = new RandomScheduler();
        linkedList = new LinkedList<String>();
        observableList = new ObservableList<String>(linkedList);

        Observer observer = new BasicObserver();
        node = new ClusterNodeWrapper(observableList, observer, ZOOKEEPER_HOST, SESSION_TIMEOUT);
        fileSystemThreadPool = new FileSystemThreadPool(CORE_POOL_SIZE, KEEP_ALIVE_TIME);

        //TODO: add a file transmission part

        rmiServer = new RmiServer(fileSystemThreadPool, scheduler, node, storageInfo);
        rmiServer.init();

        System.out.println("********* File system wrapper init success **********");
    }

    public void shutDown() {
        try {
            fileSystemThreadPool.shutDown();
            node.shutDown();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        try {
            FileSystemWrapper fileSystemWrapper = new FileSystemWrapper();
            while (true) {
                String input = scanner.nextLine();
                if (input.equals(FileSystemConstants.SHUT_DOWN.getValue())) {
                    fileSystemWrapper.shutDown();
                    break;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } catch (KeeperException exception) {
            exception.printStackTrace();
        }
    }
}
