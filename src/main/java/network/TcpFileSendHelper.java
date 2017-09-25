package network;

import network.datamodel.FileDataModel;
import network.datamodel.FileObjectModel;
import usertool.Constants;

import java.util.LinkedList;

public class TcpFileSendHelper implements Runnable {
    private LinkedList<FileObjectModel> objectQueue;
    private LinkedList<FileDataModel> fileQueue;
    private boolean shutDown;
    private long sleepInterval;

    public TcpFileSendHelper(LinkedList<FileObjectModel> objectQueue, LinkedList<FileDataModel> fileQueue) {
        this.objectQueue = objectQueue;
        this.fileQueue = fileQueue;
        shutDown = false;
        sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
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
                fileDataModel.getIp(), Constants.FILE_RECEIVE_PORT.getValue());
        synchronized (objectQueue) {
            objectQueue.add(fileObjectModel);
        }
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
