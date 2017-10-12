package network;

import network.datamodel.CommunicationDataModel;
import network.datamodel.FileDataModel;
import network.datamodel.FileObjectModel;
import usertool.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class TcpFileSendHelper implements Runnable {
    private LinkedList<Object> objectQueue;
    private LinkedList<FileDataModel> fileQueue;
    private boolean shutDown;
    private long sleepInterval;
    private String myIp;

    public TcpFileSendHelper(LinkedList<Object> objectQueue,
                             LinkedList<FileDataModel> fileQueue) throws UnknownHostException {
        this.objectQueue = objectQueue;
        this.fileQueue = fileQueue;
        shutDown = false;
        sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
        myIp = InetAddress.getLocalHost().getHostAddress();
    }

    public void addFileTask(FileDataModel fileDataModel) {
        synchronized (fileQueue) {
            fileQueue.add(fileDataModel);
        }
    }

    /**
     * We should get a not such file message if we delete sth after file transfer starts and before whole add file
     * to system finish.
     */
    private void sendFileTask() {
        FileDataModel fileDataModel;
        FileObjectModel fileObjectModel;
        synchronized (fileQueue) {
            if (!fileQueue.isEmpty()) {
                fileDataModel = fileQueue.removeFirst();
                TcpSendHelper tcpSendHelper = new TcpSendHelper(fileDataModel.getPort(), fileDataModel.getIp());
                tcpSendHelper.sendFile(fileDataModel.getFilePath());
                tcpSendHelper.clear();
            } else {
                System.out.println("********** Invalid remove attenpt on empty file queue **********");
                return;
            }
        }
        fileObjectModel = new FileObjectModel(fileDataModel.getFilePath(), Constants.ADD_FILE.getValue(),
                fileDataModel.getIp(), myIp, Constants.FILE_RECEIVE_PORT.getValue());
        synchronized (objectQueue) {
            objectQueue.add(fileObjectModel);
        }
    }

    public void shutDown() {
        shutDown = true;
    }

    public void run() {
        while(!shutDown) {
            try {
                if (fileQueue.isEmpty()) {
                    Thread.sleep(sleepInterval);
                } else {
                    sendFileTask();
                }
            } catch(InterruptedException exception) {
                break;
            }
        }
    }
}
