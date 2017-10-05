package cluster.instance;

import cluster.util.WorkerInstanceModel;
import cluster.util.WorkerReceiver;
import cluster.util.WorkerSender;
import network.datamodel.CommunicationConstants;
import network.datamodel.CommunicationDataModel;
import network.datamodel.FileDataModel;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import usertool.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Worker extends BasicWatcher {
    private String serverId;
    private String status;
    private String name;
    private WorkerSender workerSender;
    private WorkerReceiver workerReceiver;
    private LinkedList<CommunicationDataModel> comDataList;
    private LinkedList<Object> communicationSendQueue;
    private WorkerThread worker;
    private Thread workerThread;

    public Worker(String hostPort, String serverId, int sessionTimeOut,
                  WorkerSender workerSender, WorkerReceiver workerReceiver) {
        super(hostPort, sessionTimeOut);
        Random random = new Random();
        this.serverId = serverId != null ? serverId : String.valueOf(random.nextInt());
        this.name = String.format("worker-%s", serverId);
        this.workerSender = workerSender;
        this.workerReceiver = workerReceiver;
        worker = new WorkerThread();
    }

    public boolean initWorker() {
        try {
            startZooKeeper();
            register();
            workerThread = new Thread(worker);
            workerThread.start();
            return true;
        } catch (InterruptedException exception) {
            return false;
        } catch (IOException exception) {
            return false;
        }
    }

    public boolean shutDown() {
        try {
            stopZooKeeper();
            return true;
        } catch (InterruptedException exception) {
            return false;
        }
    }

    /**
     * This function gets the ip and name of the registered workers
     * @return a list of worker info
     * @throws InterruptedException
     * @throws KeeperException
     */
    public List<WorkerInstanceModel> getWorkers() throws InterruptedException, KeeperException {
        List<String> workerNameList = listNodes(Constants.WORKER_PATH.getValue());
        List<WorkerInstanceModel> workerList = new ArrayList<WorkerInstanceModel>();
        for (String workerName : workerNameList) {
            String workerPath = String.format("%s/%s", Constants.WORKER_PATH.getValue(), workerName);
            byte[] data = zooKeeper.getData(workerPath, false, null);
            String ipString = new String(data);
            workerList.add(new WorkerInstanceModel(ipString, workerName));
        }
        return workerList;
    }

    @Override
    public void process(WatchedEvent event) {
        // to be implemented
    }

    public void setStatus(String status) {
        this.status = status;
        updateStatus(status);
    }

    private class WorkerThread implements Runnable {
        public void run() {
            long sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
            String selfIp = "";
            try {
                selfIp = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException exception) {
                // not sure what to do here
            }
            CommunicationDataModel comData = null;
            while (true) {
                synchronized (comDataList) { // ???????????????/*/*/*/*/**/*/*/*/*/
                    if (comDataList.isEmpty()) {
                        comData = null;
                        try {
                            Thread.sleep(sleepInterval);
                        } catch (InterruptedException exception) {
                            // nothing to do
                        }
                    } else {
                        comData = comDataList.getFirst();
                        comDataList.removeFirst();
                    }
                }
                if (comData != null) {
                    if (comData.getAction().equals(CommunicationConstants.SEND.getValue())) {
                        // In this part, this worker is required to send file to an ip
                        FileDataModel fileDataModel = new FileDataModel(
                                comData.getActionDestinationIp(),
                                Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue()),
                                comData.getSourceFile()
                        );
                        workerSender.addFileTask(fileDataModel);
                    } else if (comData.getAction().equals(CommunicationConstants.DELETE.getValue())) {
                        // TODO: delete the file
                    } else if (comData.getAction().equals(CommunicationConstants.ADD_REPLICA.getValue())) {
                        // TODO: add replica
                    } else if (comData.getAction().equals(CommunicationConstants.ADD_REPLICAS.getValue())) {
                        // TODO: add two replicas
                    } else {
                        // TODO: I don't know...
                    }
                }
            }
        }
    }

    /**
     * Maybe we should add a return to distinguish the success of register
     */
    private void register() {
        try {
            String myIp = InetAddress.getLocalHost().getHostAddress();
            zooKeeper.create(String.format("/worker/worker-%s", serverId),
                    myIp.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT,
                    createWorkerCallback,
                    null);
        } catch (UnknownHostException exception) {
            System.err.println("*************** Error : fail to get my address ****************");
        }
    }

    private AsyncCallback.StringCallback createWorkerCallback = new AsyncCallback.StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // in this case, we retry the request
                    register();
                    break;
                case OK:
                    System.out.println("*************** Parent created ****************");
                    break;
                case NODEEXISTS:
                    System.out.println("*************** Parent already registered ****************");
                     break;
                default:
                    System.out.println(String.format("Error: %s %s",
                            KeeperException.create(KeeperException.Code.get(rc)), path));
            }
        }
    };

    private AsyncCallback.StatCallback statusUpdateCallback = new AsyncCallback.StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch(KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    updateStatus((String)ctx);
                    return;
            }
        }
    };

    synchronized private void updateStatus(String status) {
        if (status.equals(this.status)) {
            zooKeeper.setData(String.format("/workers/%s", name), status.getBytes(), -1,
                                            statusUpdateCallback, status);
        }
    }
}
