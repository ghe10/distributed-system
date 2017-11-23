package filesystem.filesystemtasks;

import cluster.ClusterNodeWrapper;
import filesystem.remoteclasses.FileSystemOperation;
import filesystem.remoteclasses.FileSystemOperationInterface;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.FileStorageDataModel;
import filesystem.serializablemodels.RmiCommunicationDataModel;
import utils.FileSystemConstants;
import utils.StaticUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * This class serves as task wrapper. Note that other RMIs may be called in this task.
 * In this version, all the operations are sequential
 */
public class FileOperationTask implements Runnable {
    private RmiCommunicationDataModel rmiCommunicationDataModel;
    //private FileStorageDataModel fileStorageDataModel;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;
    private Hashtable<String, FileStorageDataModel> storageInfo;
    private boolean succeed;

    public FileOperationTask(RmiCommunicationDataModel rmiCommunicationDataModel,
                             RandomScheduler scheduler, ClusterNodeWrapper node,
                             Hashtable<String, FileStorageDataModel> storageInfo) {
        this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.scheduler = scheduler;
        this.node = node;
        this.storageInfo = storageInfo;
        succeed = false;
    }

    public void run() {
        if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD_MAIN.getValue())) {
            // TODO : this is the main replica, add operation required, a new RMI should be defined for these operations
            addMain(rmiCommunicationDataModel.getSourceFileName(), rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD.getValue())) {
            // this is just a replica
            addFileLocal(rmiCommunicationDataModel.getSourceFileName(), rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.GET.getValue())) {
            // TODO: add some operation
            // send file
            System.out.println("Empty file send operation!");
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.DELETE.getValue())) {
            delete(rmiCommunicationDataModel.getTargetFileName());
        }
        synchronized (this) {
            // notify the waiting RMI function to return
            this.notify();
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

    private void addReplica(String replicaIp, String targetName, String sourceName, String myIp,
                               FileStorageDataModel fileStorageDataModel) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(replicaIp,
                Integer.parseInt(OperationConstants.LISTEN_PORT.getValue()));
        FileSystemOperationInterface operation = (FileSystemOperationInterface) registry.lookup(FileSystemOperation.class.getName());
        RmiCommunicationDataModel rmiCommunicationDataModel = new RmiCommunicationDataModel(targetName,
                sourceName, OperationConstants.ADD.getValue(), myIp);
        operation.replicaAddOperation(rmiCommunicationDataModel, fileStorageDataModel);
    }

    private void addMain(String sourceName, String targetName) {
        // file transmission is done before this add operation
        // this function should invoke the RMI for other replicas
        try {
            addFileLocal(sourceName, targetName);
            String myIp = StaticUtils.getLocalIp();
            HashSet<String> replicas = scheduler.randomSchedule(node.getNodeIps(),
                    new HashSet<String>(Arrays.asList(myIp)),
                    Integer.parseInt(FileSystemConstants.REPLICA_NUMBER.getValue()));
            for (String replicaIp : replicas) {
                // TODO : send file to destination
            }
            replicas.add(myIp);
            FileStorageDataModel fileStorageDataModel = new FileStorageDataModel(targetName, myIp, replicas);
            replicas.remove(myIp);
            for (String replicaIp : replicas) {
                addReplica(replicaIp, targetName, sourceName, myIp, fileStorageDataModel);
                // Since we search by accessing main replica, add these file to replica's storage info won't cause
                // consistent issue in user's point of view, so we can just directly add them when we add file
            }
            //This to do is no more required. TO DO: another for loop to add storage info to replica nodes
            synchronized (storageInfo) {
                storageInfo.put(targetName, fileStorageDataModel);
            }
            // add info to master
            if (changeMasterInfo(targetName, fileStorageDataModel)) {
                succeed = true;
            }
        } catch(UnknownHostException exception) {
            succeed = false;
        } catch (NotBoundException exception) {
            succeed = false;
        } catch (RemoteException exception) {
            succeed = false;
        }
    }

    private void addFileLocal(String sourceName, String targetName) {
        Path source = Paths.get(String.format("%s/%s",
                FileSystemConstants.TEMP_FILE_FOLDER.getValue(), sourceName));
        Path target = Paths.get(String.format("%s/%s", FileSystemConstants.MAIN_FILE_FOLDER.getValue(), targetName));
        try {
            Files.copy(source, target, StandardCopyOption.ATOMIC_MOVE);
            // TODO: the add info part should happen in the RMI all
        } catch (IOException exception) {
            exception.printStackTrace();
            succeed = false;
        }
    }

    private void delete(String targetName) {
        Path target = Paths.get(String.format("%s/%s", FileSystemConstants.MAIN_FILE_FOLDER.getValue(), targetName));
        try {
            String myIp = StaticUtils.getLocalIp();
            if (storageInfo.get(targetName).getMainReplicaIp().equals(myIp)) {
                // TODO: invoke new RMI and tell others to delete
                HashSet<String> replicas = storageInfo.get(targetName).getReplicaIps();
                // we delete file from main replica here to avoid consistency problem
                synchronized (storageInfo) {
                    storageInfo.remove(targetName);
                }
                for (String replicaIp : replicas) {
                    if (!replicaIp.equals(myIp)) {
                        // TODO: get remote name with some methods
                        Registry registry = LocateRegistry.getRegistry(replicaIp,
                                Integer.parseInt(OperationConstants.LISTEN_PORT.getValue()));
                        FileSystemOperationInterface operation =
                                (FileSystemOperationInterface) registry.lookup(FileSystemOperation.class.getName());
                        RmiCommunicationDataModel rmiCommunicationDataModel = new RmiCommunicationDataModel(targetName,
                                targetName, OperationConstants.DELETE.getValue(), myIp);
                        operation.operation(rmiCommunicationDataModel);
                    }
                }
            }
            Files.delete(target);
            if (changeMasterInfo(targetName, null)) {
                succeed = true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            succeed = false;
        } catch (NotBoundException exception) {
            exception.printStackTrace();
            succeed = false;
        }
    }

    private boolean changeMasterInfo(String name, FileStorageDataModel fileStorageDataModel) {
        try {
            FileSystemOperationInterface operationInterface =
                    (FileSystemOperationInterface) Naming.lookup("TEMP_NAME");
            operationInterface.changeMasterStorageInfoOperation(name, fileStorageDataModel);
            return true;
        } catch (RemoteException exception) {
            exception.printStackTrace();
            return false;
        } catch (NotBoundException exception) {
            exception.printStackTrace();
            return false;
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}