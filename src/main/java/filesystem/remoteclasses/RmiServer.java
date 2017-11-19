package filesystem.remoteclasses;

import cluster.ClusterNodeWrapper;
import filesystem.FileSystemThreadPool;
import filesystem.filesystemtasks.OperationConstants;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;
import utils.StaticUtils;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class RmiServer {
    private int listenPort;
    private String myIp;
    private Registry registry;
    private FileSystemThreadPool fileSystemThreadPool;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;
    private Hashtable<String, FileStorageDataModel> storageInfo;

    private FileSystemOperation fileSystemOperation;
    private FileSystemRequest fileSystemRequest;

    public RmiServer(FileSystemThreadPool fileSystemThreadPool, RandomScheduler scheduler, ClusterNodeWrapper node,
                     Hashtable<String, FileStorageDataModel> storageInfo) throws UnknownHostException, RemoteException {
        myIp = StaticUtils.getLocalIp();
        listenPort = Integer.parseInt(OperationConstants.LISTEN_PORT.getValue());
        registry = LocateRegistry.createRegistry(listenPort);
        this.fileSystemThreadPool = fileSystemThreadPool;
        this.scheduler = scheduler;
        this.node = node;
        this.storageInfo = storageInfo;
    }

    /**
     * The client will specify the ip they are working on, here we just need to set the objects
     * @return is init succeeded
     */
    public boolean init() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            fileSystemOperation = new FileSystemOperation(fileSystemThreadPool, scheduler, node, storageInfo);
            FileSystemOperationInterface fileSystemOperationInterfaceStub =
                    (FileSystemOperationInterface) UnicastRemoteObject.exportObject(fileSystemOperation, 0);
            registry.rebind(FileSystemOperation.class.getName(), fileSystemOperationInterfaceStub);
            fileSystemRequest = new FileSystemRequest(scheduler, node, storageInfo);
            FileSystemRequestInterface fileSystemRequestInterfaceStub =
                    (FileSystemRequestInterface) UnicastRemoteObject.exportObject(fileSystemRequest, 0);
            registry.rebind(FileSystemRequest.class.getName(), fileSystemRequestInterfaceStub);
        } catch (RemoteException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}
