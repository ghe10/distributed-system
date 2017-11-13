package filesystem.remoteclasses;

import cluster.ClusterNodeWrapper;
import filesystem.FileSystemThreadPool;
import filesystem.filesystemtasks.FileOperationTask;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.RmiCommunicationDataModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FileSystemOperation extends UnicastRemoteObject
        implements filesystem.remoteclasses.FileSystemOperationInterface {
    //private RmiCommunicationDataModel rmiCommunicationDataModel;
    private FileSystemThreadPool fileSystemThreadPool;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;

    public FileSystemOperation(FileSystemThreadPool fileSystemThreadPool, RandomScheduler scheduler, ClusterNodeWrapper node) throws RemoteException {
        //this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.fileSystemThreadPool = fileSystemThreadPool;
        this.scheduler = scheduler;
        this.node = node;
    }

    public boolean operation(RmiCommunicationDataModel rmiCommunicationDataModel) throws RemoteException {
        FileOperationTask task = new FileOperationTask(rmiCommunicationDataModel, scheduler, node);
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
}
