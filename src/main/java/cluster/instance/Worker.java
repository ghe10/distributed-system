package cluster.instance;

import cluster.util.WorkerInstanceModel;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import usertool.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Worker extends BasicWatcher {
    private String serverId;
    private String status;
    private String name;

    public Worker(String hostPort, String serverId, int sessionTimeOut) {
        super(hostPort, sessionTimeOut);
        Random random = new Random();
        this.serverId = serverId != null ? serverId : String.valueOf(random.nextInt());
        this.name = String.format("worker-%s", serverId);
    }

    public boolean initWorker() {
        try {
            startZooKeeper();
            register();
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
