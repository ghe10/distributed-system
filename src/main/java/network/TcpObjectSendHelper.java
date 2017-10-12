package network;

import network.datamodel.CommunicationDataModel;
import network.datamodel.FileObjectModel;
import usertool.Constants;

import java.util.LinkedList;

public class TcpObjectSendHelper extends TcpSendHelper implements Runnable {
    private LinkedList<Object> objectQueue;
    private LinkedList<CommunicationDataModel> comDataQueue;
    private boolean shutDown;
    private long sleepInterval;

    public TcpObjectSendHelper(LinkedList<Object> objectQueue, LinkedList<CommunicationDataModel> comDataQueue) {
        this.objectQueue = objectQueue;
        this.comDataQueue = comDataQueue;
        this.shutDown = false;
        sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
    }

    public void shutDown() {
        this.shutDown = true;
    }

    public LinkedList<Object> getObjectQueue() {
        return objectQueue;
    }

    public void run() {
        FileObjectModel object = null;
        while (!shutDown) {
            try {
                if (objectQueue.isEmpty()) {
                    Thread.sleep(sleepInterval);
                } else {
                    synchronized (objectQueue) {
                        object = (FileObjectModel) objectQueue.removeFirst();
                    }
                    TcpSendHelper tcpSendHelper = new TcpSendHelper(
                            Integer.parseInt(object.getPort()), object.getIp());
                    tcpSendHelper.sendObject(tcpSendHelper);
                    if (object.getComData() != null) {
                        synchronized (comDataQueue) {
                            comDataQueue.add(object.getComData());
                        }
                    }
                }
            } catch(InterruptedException exception) {
                break;
            }
        }
    }
}
