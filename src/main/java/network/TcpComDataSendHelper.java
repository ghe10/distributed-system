package network;

import network.datamodel.CommunicationDataModel;
import usertool.Constants;

import java.util.LinkedList;

/**
 * This sender is used in communication
 */
public class TcpComDataSendHelper extends TcpSendHelper implements Runnable {
    private LinkedList<CommunicationDataModel> comDataQueue;
    private boolean shutDown;
    private long sleepInterval;

    public TcpComDataSendHelper(LinkedList<CommunicationDataModel> comDataQueue) {
        this.comDataQueue = comDataQueue;
        this.shutDown = false;
        sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
    }

    public void shutDown() {
        this.shutDown = true;
    }

    public LinkedList<CommunicationDataModel> getObjectQueue() {
        return comDataQueue;
    }

    public void run() {
        CommunicationDataModel comData = null;
        while (!shutDown) {
            try {
                if (comDataQueue.isEmpty()) {
                    Thread.sleep(sleepInterval);
                } else {
                    synchronized (comDataQueue) {
                        comData = comDataQueue.removeFirst();
                    }
                    TcpSendHelper tcpSendHelper = new TcpSendHelper(
                            Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue()),
                            comData.getActionDestinationIp());
                    tcpSendHelper.sendObject(tcpSendHelper);
                }
            } catch (InterruptedException exception) {
                break;
            }
        }
    }
}

