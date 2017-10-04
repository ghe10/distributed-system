package cluster.util;

import network.TcpFileSendHelper;
import network.TcpObjectSendHelper;
import network.datamodel.FileDataModel;
import network.datamodel.FileObjectModel;

import java.util.LinkedList;

/**
 * Strategy: we send file first, when file is successfully send to destination, we send the object for metadata
 * This class serves as a wrapper of the two sender threads since these two threads should start together
 */
public class WorkerSender {
    private TcpFileSendHelper tcpFileSendHelper;
    private TcpObjectSendHelper tcpObjectSendHelper;
    private Thread fileThread;
    private Thread objectThread;

    public WorkerSender(LinkedList<Object> objectQueue, LinkedList<FileDataModel> fileQueue) {
        tcpFileSendHelper = new TcpFileSendHelper(objectQueue, fileQueue);
        tcpObjectSendHelper = new TcpObjectSendHelper(objectQueue);
        fileThread = new Thread(tcpFileSendHelper);
        objectThread = new Thread(tcpObjectSendHelper);
        fileThread.start();
        objectThread.start();
    }

    public void addFileTask(FileDataModel fileDataModel) {
        tcpFileSendHelper.addFileTask(fileDataModel);
    }

    public boolean shutDown() {
        tcpFileSendHelper.shutDown();
        tcpObjectSendHelper.shutDown();
        try {
            fileThread.join();
            objectThread.join();
        } catch (InterruptedException exception) {
            return false;
        }
        return true;
    }
}
