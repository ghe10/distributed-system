package usertool;

import cluster.instance.Client;
import cluster.instance.Master;
import cluster.instance.Worker;
import cluster.server.Server;
import cluster.util.WorkerReceiver;
import cluster.util.WorkerSender;
import network.datamodel.FileDataModel;
import network.datamodel.FileObjectModel;
import org.kohsuke.args4j.Option;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This class is the entrance of the whole system. The following functionality should be here
 * (1) get command line input to determine if this node is zooKeeper Server or worker
 * (2) start server/worker
 * (3) get user input and take correct operation
 */
public class Main {
    @Option(name = "-m", usage = "specify mode: s for server, w for worker, c for client")
    private static String mode = null;

    @Option(name = "-cfg", usage = "config path")
    private static String configPath = null;

    @Option(name = "-p", usage = "specify host port")
    private static String hostPort = null;

    private static LinkedList<Object> objectQueue;
    private static LinkedList<Object> communicationQueue;
    // this communicationQueue queue contains the info to send, the receive is handled by master's thread
    private static LinkedList<FileDataModel> fileQueue;

    private static WorkerReceiver workerReceiver = null;
    private static WorkerSender workerSender = null;

    public static void main(String[] args) {
        Thread thread = null;
        Server server = null;
        Worker worker = null;
        Master master = null;
        Client client = null;

        if (mode.equals(Constants.SERVER_MODE.getValue())) {
            configPath = configPath != null ? configPath : Constants.DEFAULT_CONFIG_PATH.getValue();
            server = new Server(configPath);
            thread = new Thread(server);
            thread.start();
        } else if (mode.equals(Constants.WORKER_MODE.getValue())) {
            objectQueue = new LinkedList<Object>();
            fileQueue = new LinkedList<FileDataModel>();
            hostPort = hostPort != null ? hostPort : Constants.DEFAULT_HOST_PORT.getValue();
            master = new Master(hostPort, Integer.parseInt(Constants.DEFAULT_SESSION_TIMEOUT.getValue()),
                    communicationQueue);
            master.runForMaster();

            workerReceiver = new WorkerReceiver(
                    Integer.parseInt(Constants.FILE_OBJECT_RECEIVE_PORT.getValue()),
                    Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue()),
                    Constants.FS_ROOT_PATH.getValue(),
                    objectQueue
            );
            workerReceiver.init();
            try {
                workerSender = new WorkerSender(objectQueue, fileQueue, communicationQueue);
            } catch (UnknownHostException exception) {
                // init failure
                System.err.println("******************* UnknownHostException ********************");
                System.exit(-1);
            }
            while (!worker.initWorker()) {
                System.out.println("******************* Try init worker ********************");
            }
            worker = new Worker(hostPort, null, Integer.parseInt(Constants.DEFAULT_SESSION_TIMEOUT.getValue()),
                    workerSender, workerReceiver);
            // we need another thread to do process the real task, I think it should be init here
        } else {
            // default is client mode
            hostPort = hostPort != null ? hostPort : Constants.DEFAULT_HOST_PORT.getValue();
            client = new Client(hostPort, Integer.parseInt(Constants.DEFAULT_SESSION_TIMEOUT.getValue()));
            while (!client.initClient()) {
                System.out.println("******************* Try init client ********************");
            }
        }

        while (true) {
            // infinite loop for user input
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            System.out.println(String.format("Input command : %s",command));
            if (command.equals(Constants.SHUT_DOWN.getValue())) {
                if (server != null) {
                    while (!server.shutDown()){ }
                    try {
                        thread.join(); // may be we don't need this
                    } catch (InterruptedException exception) {
                        System.err.println("**************** Client shut down got exception *****************");
                    }
                } else if (client != null) {
                    while (!client.stopClient()) {}
                } else {
                    // worker case
                    while (!worker.shutDown()) { }
                    while (!master.shutDown()) { }
                }
                break;
            } else {
                // other scenario
                System.out.println("******************* Invalid input ********************");
            }
        }
    }
}
