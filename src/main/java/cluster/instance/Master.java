package cluster.instance;

import network.SerializeUtil;
import network.TcpReceiveHelper;
import network.datamodel.CommunicationDataModel;
import network.datamodel.FileStorageLocalDataModel;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import scheduler.FileSystemScheduler;
import usertool.Constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Master extends BasicWatcher {

    private boolean isLeader;
    private String serverId;
    private FileSystemScheduler fileSystemScheduler;
    private LinkedList<CommunicationDataModel> masterCommunicationQueue; // this queue is not related to the one for worker
    private LinkedList<CommunicationDataModel> communicationSendQueue;
    private Hashtable<String, FileStorageLocalDataModel> fileStorageInfo; // store all the file data in memory
    private Thread masterComListerThread;
    private Thread masterThread;

    public Master(String hostPort, int sessionTimeOut, LinkedList<CommunicationDataModel> communicationSendQueue,
                  Hashtable<String, FileStorageLocalDataModel> fileStorageInfo)
            throws IOException, InterruptedException {
        super(hostPort, sessionTimeOut);
        Random random = new Random();
        serverId = String.valueOf(random.nextInt());
        masterCommunicationQueue = new LinkedList<CommunicationDataModel>();
        this.communicationSendQueue = communicationSendQueue;
        this.fileStorageInfo = fileStorageInfo;
        fileSystemScheduler = new FileSystemScheduler(
                Constants.DEFAULT_HOST_PORT.getValue(),
                Integer.parseInt(Constants.DEFAULT_SESSION_TIMEOUT.getValue()),
                Constants.RANDOM.getValue(),
                fileStorageInfo
        );
        startZooKeeper(this);
    }

    public void runForMaster() {
        masterRegistration();
        if (isLeader) {
            // I am Master !!
            bootstrap();
        }
    }

    public boolean shutDown() {
        try {
            stopZooKeeper();
            // add sth to stop the thread
            return true;
        } catch (InterruptedException exception) {
            return false;
        }
    }

    private class MasterListener extends TcpReceiveHelper {
        public MasterListener(int port) {
            super(port);
        }

        public void run() {
            byte[] byteBuffer = new byte[1024];
            while (!shutDown) {
                try {
                    Socket socket = serverSocket.accept();
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    dataInputStream.read(byteBuffer);
                    CommunicationDataModel comData = (CommunicationDataModel) SerializeUtil.deserialize(byteBuffer);
                    synchronized (masterCommunicationQueue) {
                        masterCommunicationQueue.add(comData);
                    }
                    dataInputStream.close();
                    socket.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            try {
                serverSocket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /* a queue should be in Master class for received user request*/
    private class MasterThread implements Runnable {
        // this thread should deal with client request from a queue

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
                synchronized (masterCommunicationQueue) { // ???????????????/*/*/*/*/**/*/*/*/*/
                    if (masterCommunicationQueue.isEmpty()) {
                        comData = null;
                        try {
                            Thread.sleep(sleepInterval);
                        } catch (InterruptedException exception) {
                            // nothing to do
                        }
                    } else {
                        comData = masterCommunicationQueue.getFirst();
                        masterCommunicationQueue.removeFirst();
                    }
                }
                if (comData != null) {
                    String action = comData.getAction();
                    if (action.equals(Constants.ADD_FILE.getValue())) {
                        // TODO: use some algorithm to find primary replica
                        // client will send to main replica, replication will be done by main replica
                        String mainReplicaIp = fileSystemScheduler.scheduleMainReplica(comData.getFileSize());
                        CommunicationDataModel ack = new CommunicationDataModel(selfIp, comData.getSenderIp(),
                                mainReplicaIp, Constants.ADD_FILE_ACK.getValue(), "", "",
                                Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())); // sth should be here
                        synchronized (communicationSendQueue) {
                            communicationSendQueue.add(ack);
                        }

                    } else if (action.equals(Constants.REQUEST_FILE.getValue())) {
                        // TODO: use some algorithm to choose the correct replica
                        // tell a chosen replica to send file to requester
                        String chosenReplicaIp = fileSystemScheduler.scheduleFileGet(comData.getSourceFile());
                        CommunicationDataModel ack = new CommunicationDataModel(selfIp, chosenReplicaIp,
                                comData.getSenderIp(), Constants.ADD_FILE_ACK.getValue(), "", "",
                                Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())); // sth should be here
                        synchronized (communicationSendQueue) {
                            communicationSendQueue.add(ack);
                        }

                    } else if (action.equals(Constants.REMOVE_FILE.getValue())) {
                        FileStorageLocalDataModel fileStorageLocalDataModel =
                                fileStorageInfo.getOrDefault(comData.getSourceFile(), null);
                        String mainReplicaIp = "";
                        if (fileStorageLocalDataModel != null) {
                            mainReplicaIp = fileStorageLocalDataModel.getMainReplicaIp();
                        }
                        CommunicationDataModel ack = new CommunicationDataModel(selfIp, mainReplicaIp,
                                comData.getSenderIp(), Constants.ADD_FILE_ACK.getValue(), "", "",
                                Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue())); // sth should be here
                        synchronized (communicationSendQueue) {
                            communicationSendQueue.add(ack);
                        }
                        synchronized (fileStorageInfo) {
                            fileStorageInfo.remove(comData.getSourceFile());
                        }
                        // the delete operation is not yet done, the file will be deactivated when ack form main replica??
                        // may be delete it now is better
                    } else {
                        System.out.println("*************** Incorrect input *****************");
                    }
                }
            }
        }
    }
    private String getMainReplicaIp(String fileName) {
        if (fileStorageInfo.containsKey(fileName)) {
            return fileStorageInfo.get(fileName).getMainReplicaIp();
        }
        return null;
    }

    private void bootstrap() {
        // this function will create a bunch of folders in zookeeper for coordination
        createParent("/workers", new byte[0]);
        createParent("/assign", new byte[0]);
        createParent("/tasks", new byte[0]);
        createParent("/status", new byte[0]);
        int port = Integer.parseInt(Constants.MASTER_COMMUNICATION_PORT.getValue());
        MasterListener listener = new MasterListener(port);
        MasterThread thread = new MasterThread();
        masterComListerThread = new Thread(listener);
        masterThread = new Thread(thread);
        masterComListerThread.start();
        masterThread.start();
    }

    /**
     * This function deal with the master related events. Now it should include master failure
     * @param event : event received from zooKeeper servers
     */
    @Override
    public void process(WatchedEvent event) {
        // to be implemented
        if (event.getType() == Event.EventType.NodeDeleted) {
            runForMaster();
        }
    }

    public boolean existMaster() {
        while (true) {
            try {
                Stat stat = new Stat();
                byte data[] = zooKeeper.getData("/master", false, stat);
                isLeader = new String(data).equals(serverId);
                return true;
            } catch(InterruptedException exception) {
                return false;
            } catch(KeeperException exception) {
                // do nothing
            }
        }
    }

    private void masterRegistration() {
        while (true) {
            try {
                zooKeeper.create("/master", serverId.getBytes(), OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                isLeader = true;
                break;
            } catch (KeeperException.NodeExistsException exception) {
                isLeader = false;
                break;
            }  catch (InterruptedException exception) {
                isLeader = false;
                break;
            } catch (KeeperException.ConnectionLossException exception) {
                // do nothing, we should try again
            } catch (KeeperException exception) {
                isLeader = false;
                break;
            }
            if (existMaster()) {
                isLeader = false;
                break;
            }
        }
    }

    private void createParent(String path, byte[] data) {
        zooKeeper.create(path, data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createParentCallback,
                data);
    }

    private AsyncCallback.StringCallback createParentCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (KeeperException.Code.get(rc)) {
                case CONNECTIONLOSS:
                    // in this case, we retry the request
                    createParent(path, (byte[]) ctx);
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
}
