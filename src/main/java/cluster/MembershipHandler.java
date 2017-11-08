package cluster;

import java.util.LinkedList;

public class MembershipHandler implements Runnable {
    private LinkedList<String> deadEventQueue;
    private boolean shutDown;

    public MembershipHandler(LinkedList<String> deadEventQueue) {
        this.deadEventQueue = deadEventQueue;
        shutDown = false;
    }

    public void shutDown() {
        shutDown = true;
        // tell event to stop
        deadEventQueue.notifyAll();
    }

    public void run() {
        while (!shutDown) {
            synchronized (deadEventQueue) {
                while(deadEventQueue.size() == 0) {
                    try {
                        deadEventQueue.wait();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    process();
                }
            }
        }
    }

    protected void process() {
        // use thread pool to process dead event
    }
}
