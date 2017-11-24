package filesystem;

import cluster.ClusterNodeWrapper;
import filesystem.filesystemtasks.FileOperationTask;
import filesystem.filesystemtasks.OperationConstants;
import filesystem.remoteclasses.RmiServer;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;
import filesystem.serializablemodels.RmiCommunicationDataModel;

import java.rmi.UnknownHostException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

public class FileSystemObserver implements Observer {
    private Hashtable<String, FileStorageDataModel> storageInfo;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;
    private String myIp;
    FileSystemThreadPool fileSystemThreadPool;

    public void update(Observable observable, Object object) {
        System.out.println("File system update is called");
        System.out.println((String) object);

        String deadIp = (String) object;

        for (String file : storageInfo.keySet()) {
            if (storageInfo.get(file).getMainReplicaIp().equals(myIp) &&
                    storageInfo.get(file).getReplicaIps().contains(deadIp)) {
                RmiCommunicationDataModel rmiCommunicationDataModel = new RmiCommunicationDataModel(file, file,
                        OperationConstants.FAILURE.getValue(), myIp);
                try {
                    FileOperationTask task = new FileOperationTask(rmiCommunicationDataModel, scheduler, node, storageInfo);
                    fileSystemThreadPool.addTask(task);
                } catch (java.net.UnknownHostException exception) {
                    exception.printStackTrace();
                }
            } else if (storageInfo.get(file).getReplicaIps().contains(deadIp)){
                synchronized (storageInfo) {
                    storageInfo.get(file).getReplicaIps().remove(deadIp);
                }
            }
        }
    }
}
