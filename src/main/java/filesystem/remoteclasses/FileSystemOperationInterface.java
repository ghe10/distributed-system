package filesystem.remoteclasses;

import filesystem.serializablemodels.FileStorageDataModel;
import filesystem.serializablemodels.RmiCommunicationDataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemOperationInterface extends Remote {
    boolean operation(RmiCommunicationDataModel rmiCommunicationDataModel) throws RemoteException;

    boolean replicaAddOperation(RmiCommunicationDataModel rmiCommunicationDataModel,
                             FileStorageDataModel fileStorageDataModel) throws RemoteException;

    void changeMasterStorageInfoOperation(String fileName, FileStorageDataModel fileStorageDataModel)
            throws RemoteException;
}
