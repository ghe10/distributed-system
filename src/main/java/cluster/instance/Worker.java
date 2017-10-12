package cluster.instance;

import cluster.util.WorkerInstanceModel;
import cluster.util.WorkerReceiver;
import cluster.util.WorkerSender;
import network.datamodel.*;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import scheduler.FileSystemScheduler;
import usertool.Constants;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * Short :
 * (1) watcher function for replication
 * (2) inform the new replica about the storage status of this file
 * (3) inform master about the new replica storage statue
 */
public class Worker extends BasicWatcher {
    private String serverId;
    private String status;
    private String name;
    private String myIp;
    private WorkerSender workerSender;
    private WorkerReceiver workerReceiver;
    private FileSystemScheduler fileSystemScheduler;
    private LinkedList<CommunicationDataModel> comDataQueue;
    private LinkedList<CommunicationDataModel> communicationSendQueue;
    private LinkedList<Object> fileSystemObjectQueue;
    private Hashtable<String, FileStorageLocalDataModel> fileStorageInfo;
    private WorkerThread worker;
    private Thread workerThread;

    public Worker(String hostPort, String serverId, int sessionTimeOut,
                  WorkerSender workerSender, WorkerReceiver workerReceiver,
                  Hashtable<String, FileStorageLocalDataModel> fileStorageInfo) throws UnknownHostException {
        super(hostPort, sessionTimeOut);
        Random random = new Random();
        this.serverId = serverId != null ? serverId : String.valueOf(random.nextInt());
        this.name = String.format("worker-%s", serverId);
        this.workerSender = workerSender;
        this.workerReceiver = workerReceiver;
        this.fileStorageInfo = fileStorageInfo;
        fileSystemObjectQueue = workerReceiver.objectQueue;
        worker = new WorkerThread();
        fileSystemScheduler = new FileSystemScheduler(hostPort, sessionTimeOut,
                    workerSender, workerReceiver, Constants.RANDOM.getValue());
        myIp = InetAddress.getLocalHost().getHostAddress();
        comDataQueue = new LinkedList<CommunicationDataModel>();
        communicationSendQueue = workerSender.getCommunicationQueue();
    }

    public boolean initWorker() {
        try {
            startZooKeeper(this);
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

    private void deleteFile(String fileName) {
        if (fileStorageInfo.containsKey(fileName)) {
            FileStorageLocalDataModel storageInfo = fileStorageInfo.get(fileName);
            File file = new File(String.format("%s%s", Constants.FS_ROOT_PATH.getValue(), fileName));
            file.delete();
            if (storageInfo.isMainReplica(myIp)) {
                for (String replicaIp : storageInfo.getReplicaIps()) {
                    if (replicaIp.equals(myIp)) {
                        continue;
                    }
                    CommunicationDataModel comData = new CommunicationDataModel(
                            myIp, replicaIp,
                            CommunicationConstants.DELETE.getValue(),
                            fileName,
                            fileName,
                            Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())
                    );
                    synchronized (communicationSendQueue) {
                        communicationSendQueue.add(comData);
                    }
                }
            }
            synchronized (fileStorageInfo) {
                fileStorageInfo.remove(fileName);
            }
        }
    }

    private HashSet<String> addReplica(HashSet<String> existingReplicaIps, String filePath, long fileSize) {
        int replicaGap =  Integer.parseInt(Constants.REPLICATION_NUM.getValue()) + 1 - existingReplicaIps.size();
        String masterIp = "";

        if (replicaGap == 0) {
            return existingReplicaIps;
        }
        ArrayList<String> replicaIps = fileSystemScheduler.scheduleFile(fileSize,
                existingReplicaIps, replicaGap);
        for (String replicaIp : replicaIps) {
            CommunicationDataModel comDataToSend = new CommunicationDataModel(
                    myIp, replicaIp,
                    myIp,
                    Constants.SET_PRIMARY_REPLICA.getValue(),
                    filePath,
                    filePath,
                    Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())
            );
            comDataToSend.addReplicaInfo(myIp, new HashSet<String>(replicaIps));
            FileDataModel addReplicaTask = new FileDataModel(
                    replicaIp,
                    Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue()),
                    filePath,
                    comDataToSend
            );
            workerSender.addFileTask(addReplicaTask);
        }
        // TODO: get master IP
        // send ack to master about the info
        replicaIps.addAll(existingReplicaIps);
        ackForReplicationChange(masterIp, filePath, new HashSet<String>(replicaIps));
        return new HashSet<String>(replicaIps);
    }

    /**
     * This function sends ack for replication change to master for multiple reasons:
     * 1. replication change
     * 2. new file is added
     * */
    private void ackForReplicationChange(String masterIp, String filePath, HashSet<String> replicaIps) {
        CommunicationDataModel comDataToSend = new CommunicationDataModel(
                myIp, masterIp,
                Constants.SET_PRIMARY_REPLICA.getValue(),
                filePath,
                filePath,
                Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())
        );
        comDataToSend.addReplicaInfo(myIp, replicaIps);
        synchronized (communicationSendQueue) {
            communicationSendQueue.add(comDataToSend);
        }
    }

    private class WorkerThread implements Runnable {
        public void run() {
            long sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
            CommunicationDataModel comData = null;
            while (true) {
                synchronized (comDataQueue) { // ???????????????/*/*/*/*/**/*/*/*/*/
                    if (comDataQueue.isEmpty()) {
                        comData = null;
                        try {
                            Thread.sleep(sleepInterval);
                        } catch (InterruptedException exception) {
                            // nothing to do
                        }
                    } else {
                        comData = comDataQueue.getFirst();
                        comDataQueue.removeFirst();
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
                        deleteFile(comData.getSourceFile());
                    } else if (comData.getAction().equals(CommunicationConstants.GET_FILE.getValue())) {
                        FileDataModel fileDataModel = new FileDataModel(
                                comData.getActionDestinationIp(),
                                Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue()),
                                comData.getSourceFile()
                        );
                        workerSender.addFileTask(fileDataModel);
                    } else if (comData.getAction().equals(Constants.SET_PRIMARY_REPLICA.getValue())) {
                        // set primary, this results in add replica
                        FileStorageLocalDataModel storageInfo = null;
                        synchronized (fileStorageInfo) {
                            if (!fileStorageInfo.containsKey(comData.getSourceFile())) {
                                // this file might be deleted already
                                continue;
                            }
                            storageInfo = fileStorageInfo.get(comData.getSourceFile());
                            storageInfo.setMainReplica(comData.getActionDestinationIp(), myIp);
                            if (storageInfo.isMainReplica(myIp)) {
                                HashSet<String> replicaIps = addReplica(storageInfo.getReplicaIps(),
                                        comData.getSourceFile(), comData.getFileSize());
                                storageInfo.setReplicas(replicaIps);
                            }
                            fileStorageInfo.put(comData.getSourceFile(), storageInfo);
                        }
                    } else {
                        // TODO: I don't know...
                    }
                }
            }
        }
    }

    private class WorkerFileSystemThread implements Runnable {
        public void run() {
            long sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
            FileObjectModel fileSystemData = null;
            while (true) {
                synchronized (fileSystemObjectQueue) { // ???????????????/*/*/*/*/**/*/*/*/*/
                    if (fileSystemObjectQueue.isEmpty()) {
                        fileSystemData = null;
                        try {
                            Thread.sleep(sleepInterval);
                        } catch (InterruptedException exception) {
                            // nothing to do
                        }
                    } else {
                        fileSystemData = (FileObjectModel)fileSystemObjectQueue.getFirst();
                        fileSystemObjectQueue.removeFirst();
                    }
                }
                if (fileSystemData != null) {
                    if (fileSystemData.getActionType().equals(Constants.PUT_PRIMARY_REPLICA.getValue())) {
                        ArrayList<String> replicaIps = fileSystemScheduler.scheduleFile(0L, null,
                                Integer.parseInt(Constants.REPLICATION_NUM.getValue()));
                        for (String replicaIp : replicaIps) {
                            FileDataModel addReplicaTask = new FileDataModel(
                                    replicaIp,
                                    Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue()),
                                    fileSystemData.getFilePath()
                            );
                            workerSender.addFileTask(addReplicaTask);
                        }
                        replicaIps.add(myIp);
                        FileStorageLocalDataModel fileStorageLocalDataModel = new FileStorageLocalDataModel(
                                fileSystemData.getFilePath(),
                                myIp,
                                0L,
                                true,
                                new HashSet<String>(replicaIps)
                        );

                        /**
                         * We have one write and maybe multiple reads
                         * do we need lock????
                         * */
                        fileStorageInfo.put(fileSystemData.getFilePath(), fileStorageLocalDataModel);
                        // TODO: get masterIp here
                        String masterIp = "";
                        CommunicationDataModel ackToMaster = new CommunicationDataModel(
                                myIp, masterIp, fileSystemData.getSenderIp(),
                                Constants.ADD_FILE_ACK.getValue(),
                                fileSystemData.getFilePath(),
                                fileSystemData.getFilePath(),
                                Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())
                        );
                        synchronized (comDataQueue) {
                            comDataQueue.add(ackToMaster);
                        }
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
