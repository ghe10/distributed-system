package cluster.instance;

import network.datamodel.NodeInfoModel;
import org.apache.zookeeper.*;
import usertool.Constants;

import java.io.IOException;
import java.util.List;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class Client extends BasicWatcher {

    public Client(String hostPort, int sessionTimeOut) {
        super(hostPort, sessionTimeOut);
    }

    public boolean initClient() {
        try {
            startZooKeeper();
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

    

}
