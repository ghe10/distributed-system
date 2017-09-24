package cluster.util;

import network.TcpFileReceiveHelper;
import network.TcpObjectReceiveHelper;

import java.util.LinkedList;

/**
 * All the metadata object should extend a base class, that class should has an entry with actual class type
 */
public class WorkerReceiver {
    private int filePort;
    private int objectPort;
    private String fileFolder;

    private TcpFileReceiveHelper tcpFileReceiveHelper;
    private TcpObjectReceiveHelper tcpObjectReceiveHelper;

    private Thread tcpFileReceiverThread;
    private Thread tcpObjectReceiverThread;

    private LinkedList<Object> objectQueue;

    public WorkerReceiver(int objectPort, int filePort, String fileFolder, LinkedList<Object> objectQueue) {
        this.filePort = filePort;
        this.objectPort = objectPort;
        this.fileFolder = fileFolder;
        this.objectQueue = objectQueue;
    }

    public void init() {
        tcpObjectReceiveHelper = new TcpObjectReceiveHelper(objectPort, objectQueue);
        tcpFileReceiveHelper = new TcpFileReceiveHelper(filePort, fileFolder);
        tcpObjectReceiverThread = new Thread(tcpObjectReceiveHelper);
        tcpFileReceiverThread = new Thread(tcpFileReceiveHelper);
        tcpObjectReceiverThread.start();
        tcpFileReceiverThread.start();
    }

    public boolean shutDown() {
        tcpFileReceiveHelper.shutDownHelper();
        tcpObjectReceiveHelper.shutDownHelper();
        try {
            tcpFileReceiverThread.join();
            tcpObjectReceiverThread.join();
            return true;
        } catch (InterruptedException exception) {
            return false;
        }
    }
}
