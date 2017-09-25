package network;

import network.datamodel.FileObjectModel;
import usertool.Constants;

import java.util.LinkedList;

public class TcpObjectSendHelper extends TcpSendHelper implements Runnable {
    private LinkedList<FileObjectModel> objectQueue;
    private boolean shutDown;
    private long sleepInverval;

    public TcpObjectSendHelper(LinkedList<FileObjectModel> objectQueue) {
        this.objectQueue = objectQueue;
        this.shutDown = false;
        sleepInverval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
    }

    public void shutDown() {
        this.shutDown = true;
    }

    public void run() {
        while (!shutDown) {
            try {
                if (objectQueue.isEmpty()) {
                    Thread.sleep(sleepInverval);
                } else {
                    FileObjectModel object = objectQueue.removeFirst();
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
