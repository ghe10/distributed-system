package cluster.instance;

import network.TcpReceiveHelper;
import network.TcpSendHelper;
import network.datamodel.CommunicationDataModel;
import network.datamodel.NodeInfoModel;
import org.apache.zookeeper.*;
import usertool.Constants;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Client extends BasicWatcher {
    private TcpReceiveHelper tcpReceiveHelper;

    public Client(String hostPort, int sessionTimeOut) {
        super(hostPort, sessionTimeOut);
    }

    public boolean initClient() {
        int listenPort = Integer.parseInt(Constants.CLIENT_COMMUNICATION_PORT.getValue());
        try {
            startZooKeeper();
            tcpReceiveHelper = new TcpReceiveHelper(listenPort);
            return true;
        } catch(InterruptedException exception) {
            return false;
        } catch(IOException exception) {
            return false;
        }
    }

    public boolean stopClient() {
        try {
            stopZooKeeper();
            return true;
        } catch (InterruptedException exception) {
            return false;
        }
    }

    /*
     * This function might be used for computation in the future
     * @param command
     * @return
     * @throws KeeperException
     */
    public String queueCommand(String command) throws KeeperException{
        while (true) {
            try {
                String name = zooKeeper.create("/task/task-", command.getBytes(),
                        OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                return name;
            } catch (InterruptedException exception) {
                System.err.println("*************** Interrupt exception in queueCommand ****************");
            }
        }
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
    }

    /*
     * The following functions are for file put and delete
     * put steps:
     * 1. ask zookeeper to get master
     * 2. send request to master
     * 3. master find one "prime" replica node and send back the info
     * 4. send file to replica node
     * 5. send dataModel
     * 6. the "prime" replica compute and find the two secondary replicas
     * 7. "prime" replica send ack to client
     */


    /*This function gets the master info*/
    private NodeInfoModel getMasterInfo() {
        try {
            //List<String> MasterList = zooKeeper.getChildren(Constants.MASTER_PATH.getValue(), false);
            //String masterPath
            byte[] data = zooKeeper.getData(Constants.MASTER_PATH.getValue(), false, null);
            String ip = new String(data);
            NodeInfoModel nodeInfoModel = new NodeInfoModel(ip);
            return nodeInfoModel;
        } catch (InterruptedException exception) {
            System.err.println("*************** Interrupt exception in getMasterInfo ****************");
            return null;
        } catch (KeeperException exception) {
            System.err.println("*************** Keeper exception in getMasterInfo ****************");
            return null;
        }
    }

    private CommunicationDataModel sendRequest(String masterIp, CommunicationDataModel data) {
        int masterListenPort = Integer.parseInt(Constants.MASTER_COMMUNICATION_PORT.getValue());
        TcpSendHelper tcpSendHelper = new TcpSendHelper(masterListenPort, masterIp);
        tcpSendHelper.sendObject(data);
        return (CommunicationDataModel)tcpReceiveHelper.receive(0); // 0 is interpreted as infinite
    }

    /* if interrupted the put should fail */
    public boolean putFile(String path) throws InterruptedException {
        TcpSendHelper tcpSendHelper = null;
        NodeInfoModel masterInfo = null;
        CommunicationDataModel primaryReplicaInfo = null;
        CommunicationDataModel putRequestInfo = null;
        CommunicationDataModel putResult = null;
        int retry = Integer.parseInt(Constants.GET_MASTER_RETRY.getValue());
        int workerFileReceivePort = Integer.parseInt(Constants.FILE_RECEIVE_PORT.getValue());
        int putTimeOut = Integer.parseInt(Constants.PUT_TIME_OUT.getValue());
        long sleepInterval = Long.parseLong(Constants.SLEEP_INTERVAL.getValue());
        for (; retry > 0; retry--) {
            masterInfo = getMasterInfo();
            if (masterInfo != null) {
                break;
            }
            Thread.sleep(sleepInterval);
        }
        // if we fail to get master, mission filed
        if (masterInfo == null) return false;
        // TODO: init putRequest
        primaryReplicaInfo = sendRequest(masterInfo.getIp(), putRequestInfo);
        // TODO: check if allowed to put file
        tcpSendHelper = new TcpSendHelper(workerFileReceivePort, primaryReplicaInfo.getSenderIp());
        tcpSendHelper.sendFile(path);
        // Next we should try to receive
        putResult = (CommunicationDataModel) tcpReceiveHelper.receive(putTimeOut);
        if (putResult == null) {
            return false;
        }
        return true;
    }

}
