package network;

import java.util.LinkedList;

public class TcpObjectSendHelper extends TcpSendHelper implements Runnable {
    private LinkedList<Object> objectQueue;

    public TcpObjectSendHelper(LinkedList<Object> objectQueue) {
        this.objectQueue = objectQueue;
    }

    public void run() {

    }

}
