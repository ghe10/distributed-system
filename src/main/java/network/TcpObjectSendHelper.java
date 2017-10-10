package network;

import network.datamodel.FileObjectModel;
import usertool.Constants;

import java.util.LinkedList;

public class TcpObjectSendHelper extends TcpSendHelper implements Runnable {
    private LinkedList<Object> objectQueue;
    private boolean shutDown;
    private long sleepInterval;

    public TcpObjectSendHelper(LinkedList<Object> objectQueue) {
        this.objectQueue = objectQueue;
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
        while (!shutDown) {
            try {
                if (objectQueue.isEmpty()) {
                    Thread.sleep(sleepInterval);
                } else {
                    FileObjectModel object = (FileObjectModel) objectQueue.removeFirst();
                    TcpSendHelper tcpSendHelper = new TcpSendHelper(
                            Integer.parseInt(object.getPort()), object.getIp());
                    tcpSendHelper.sendObject(tcpSendHelper);
                }
            } catch(InterruptedException exception) {
                break;
            }
        }
    }
}
