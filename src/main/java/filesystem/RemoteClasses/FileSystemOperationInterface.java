package filesystem.RemoteClasses;

import filesystem.serializablemodels.RmiCommunicationDataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemOperationInterface extends Remote {
    public boolean operation(RmiCommunicationDataModel rmiCommunicationDataModel) throws RemoteException;
}
