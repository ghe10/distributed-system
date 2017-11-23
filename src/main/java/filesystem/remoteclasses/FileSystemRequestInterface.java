package filesystem.remoteclasses;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemRequestInterface extends Remote {
    // the following call is designed for operation on existing files
    String getMainReplicaIp(String fileName) throws RemoteException;
    // the following call is designed for add new file
    String scheduleMainReplica() throws RemoteException;
}
