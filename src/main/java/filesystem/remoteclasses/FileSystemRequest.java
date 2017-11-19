package filesystem.remoteclasses;

import cluster.ClusterNodeWrapper;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * All the user requests can be separated in two cases:
 * 1. Get main replica ip
 * 2. Schedule a new main replica ip
 */
public class FileSystemRequest extends UnicastRemoteObject
        implements FileSystemRequestInterface {
    private Hashtable<String, FileStorageDataModel> storageInfo;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;

    public FileSystemRequest(RandomScheduler scheduler, ClusterNodeWrapper node,
                             Hashtable<String, FileStorageDataModel> storageInfo) throws RemoteException {
        this.scheduler = scheduler;
        this.storageInfo = storageInfo;
        this.node = node;
    }

    public String getMainReplicaIp(String fileName) throws RemoteException {
        // may be we can remove the first condition
        if (storageInfo.containsKey(fileName) && storageInfo.get(fileName) != null) {
            return storageInfo.get(fileName).getMainReplicaIp();
        } else {
            // we don't have this replica
            return null;
        }
    }

    public String scheduleMainReplica() throws RemoteException {
        HashSet<String> nodeIps = node.getNodeIps();
        return scheduler.randomSchedule(nodeIps, null);
    }
}
