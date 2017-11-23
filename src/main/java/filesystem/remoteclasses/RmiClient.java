package filesystem.remoteclasses;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {

    private String getMasterIp() {
        // TODO: getMasterIp
        return "tmp";
    }

    private boolean operation() {
        try {
            String masterIp = getMasterIp();
            Registry registry = LocateRegistry.getRegistry(masterIp, 2020);
            //FileSystemRequestInterface fileSystemRequestInterface = (FileSystemRequestInterface) registry.lookup(masterIp);
            return true;
        } catch (RemoteException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void main(String args[]) {
        // TODO: operation

    }

}
