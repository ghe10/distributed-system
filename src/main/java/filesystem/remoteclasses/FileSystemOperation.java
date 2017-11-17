package filesystem.remoteclasses;

import cluster.ClusterNodeWrapper;
import filesystem.FileSystemThreadPool;
import filesystem.filesystemtasks.FileOperationTask;
import filesystem.filesystemtasks.OperationConstants;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;
import filesystem.serializablemodels.RmiCommunicationDataModel;
import utils.FileSystemConstants;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

/**
 * When we start the server, we can add parameters during bind.
 */
public class FileSystemOperation extends UnicastRemoteObject
        implements filesystem.remoteclasses.FileSystemOperationInterface {
    //private RmiCommunicationDataModel rmiCommunicationDataModel;
    private FileSystemThreadPool fileSystemThreadPool;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;
    private Hashtable<String, FileStorageDataModel> storageInfo;

    public FileSystemOperation(FileSystemThreadPool fileSystemThreadPool, RandomScheduler scheduler,
                               ClusterNodeWrapper node, Hashtable<String, FileStorageDataModel> storageInfo)
            throws RemoteException {
        //this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.fileSystemThreadPool = fileSystemThreadPool;
        this.scheduler = scheduler;
        this.node = node;
        this.storageInfo = storageInfo;
    }

    public boolean operation(RmiCommunicationDataModel rmiCommunicationDataModel) throws RemoteException {
        FileOperationTask task = new FileOperationTask(rmiCommunicationDataModel, scheduler, node, storageInfo);
        fileSystemThreadPool.addTask(task);
        // TODO: do sth to check if the task finishes
        /**
         * may be we can use protected void afterExecute(Runnable r,
         * Throwable t) and instance of along with an observer to do this job
         * The observer should be part of the task runnable
         *
         * Maybe wait-notify is enough
         *
         * Now we are sync on local variables. Even if multiple concurrent calls on this remote object, there won't be
         * a problem about task wait
         */
        synchronized (task) {
            try {
                task.wait();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * This function is defined for replica info add only, i.e. pass the file storage data model to remote
     */
    public boolean replicaAddOperation(FileStorageDataModel fileStorageDataModel) throws RemoteException {
        if (fileStorageDataModel == null) {
            return false;
        }
        synchronized (storageInfo) {
            storageInfo.put(fileStorageDataModel.getFileName(), fileStorageDataModel);
            return true;
        }
    }
}
