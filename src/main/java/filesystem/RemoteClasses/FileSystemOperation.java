package filesystem.RemoteClasses;

import filesystem.FileSystemThreadPool;
import filesystem.filesystemtasks.FileOperationTask;
import filesystem.serializablemodels.RmiCommunicationDataModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FileSystemOperation extends UnicastRemoteObject implements FileSystemOperationInterface {
    //private RmiCommunicationDataModel rmiCommunicationDataModel;
    private FileSystemThreadPool fileSystemThreadPool;
    private FileOperationTask task;

    public FileSystemOperation(RmiCommunicationDataModel rmiCommunicationDataModel,
                               FileSystemThreadPool fileSystemThreadPool) throws RemoteException {
        //this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.fileSystemThreadPool = fileSystemThreadPool;
    }

    public boolean operation(RmiCommunicationDataModel rmiCommunicationDataModel) throws RemoteException {
        task = new FileOperationTask(rmiCommunicationDataModel);
        fileSystemThreadPool.addTask(task);
        // TODO: do sth to check if the task finishes
        /**
         * may be we can use protected void afterExecute(Runnable r,
         * Throwable t) and instance of along with an observer to do this job
         * The observer should be part of the task runnable
         *
         * Maybe wait-notify is enough
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
