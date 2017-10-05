package cluster.util;

import network.TcpFileSendHelper;
import network.TcpObjectSendHelper;
import network.datamodel.CommunicationDataModel;
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
    private TcpObjectSendHelper communicationHelper;
    private Thread fileThread;
    private Thread objectThread;
    private Thread communicationThread;

    public WorkerSender(LinkedList<Object> objectQueue, LinkedList<FileDataModel> fileQueue,
                        LinkedList<Object> communicationQueue) {
        tcpFileSendHelper = new TcpFileSendHelper(objectQueue, fileQueue);
        tcpObjectSendHelper = new TcpObjectSendHelper(objectQueue);
        communicationHelper = new TcpObjectSendHelper(communicationQueue);
        fileThread = new Thread(tcpFileSendHelper);
        objectThread = new Thread(tcpObjectSendHelper);
        communicationThread = new Thread(communicationHelper);
        fileThread.start();
        objectThread.start();
        communicationThread.start();
    }

    public void addFileTask(FileDataModel fileDataModel) {
        tcpFileSendHelper.addFileTask(fileDataModel);
    }

    public boolean shutDown() {
        tcpFileSendHelper.shutDown();
        tcpObjectSendHelper.shutDown();
        communicationHelper.shutDown();
        try {
            fileThread.join();
            objectThread.join();
            communicationThread.join();
        } catch (InterruptedException exception) {
            return false;
        }
        return true;
    }
}
