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
    private String myIp;

    private static final int REPLICA_NUM = 2;

    public FileOperationTask(RmiCommunicationDataModel rmiCommunicationDataModel,
                             RandomScheduler scheduler, ClusterNodeWrapper node,
                             Hashtable<String, FileStorageDataModel> storageInfo) throws UnknownHostException {
        this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.scheduler = scheduler;
        this.node = node;
        this.storageInfo = storageInfo;
        succeed = false;
        String myIp = StaticUtils.getLocalIp();
    }

    public void run() {
        if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD_MAIN.getValue())) {
            // TODO : this is the main replica, add operation required, a new RMI should be defined for these operations
            System.out.println("Add main replica");
            addMain(rmiCommunicationDataModel.getSourceFileName(), rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD.getValue())) {
            // this is just a replica
            System.out.println("Add file to local");
            addFileLocal(rmiCommunicationDataModel.getSourceFileName(), rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.GET.getValue())) {
            // TODO: add some operation
            // send file
            System.out.println("Empty file send operation!");
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.DELETE.getValue())) {
            System.out.println("Delete");
            delete(rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.SET_MAIN.getValue())) {
            setMain(rmiCommunicationDataModel.getTargetFileName());
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.FAILURE.getValue())) {
            failure(rmiCommunicationDataModel.getTargetFileName());
        }
        synchronized (this) {
            // notify the waiting RMI function to return
            this.notify();
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

    private void failure(String fileName) {
        FileStorageDataModel fileStorageDataModel = storageInfo.get(fileName);
        if (fileStorageDataModel.getReplicaIps().size() < REPLICA_NUM) {
            HashSet<String> existingReplicas = fileStorageDataModel.getReplicaIps();
            HashSet<String> newReplicaIps = scheduler.randomSchedule(node.getNodeIps(), fileStorageDataModel.getReplicaIps(),
                    REPLICA_NUM - fileStorageDataModel.getReplicaIps().size());
            fileStorageDataModel.addReplicaIPs(newReplicaIps);
            try {
                for (String ip : newReplicaIps) {
                    // TODO: send file
                    addReplica(ip, fileName, fileName, myIp, fileStorageDataModel);
                    // TODO: I think we need to deal with failure
                }
                for (String ip : existingReplicas) {
                    changeMasterInfo(fileName, fileName, null);
                }
            } catch (RemoteException exception) {
                exception.printStackTrace();
                return;
            } catch (NotBoundException exception) {
                exception.printStackTrace();
                return;
            }
            synchronized (storageInfo) {
                storageInfo.put(fileName, fileStorageDataModel);
            }
        }
    }

    private void setMain(String fileName) {
        if (storageInfo.containsKey(fileName)) {
            storageInfo.get(fileName).setMainReplicaIp(myIp);
            FileStorageDataModel fileStorageDataModel = storageInfo.get(fileName);
            if (fileStorageDataModel.getReplicaIps().size() < REPLICA_NUM) {
                HashSet<String> newReplicaIps = scheduler.randomSchedule(node.getNodeIps(), fileStorageDataModel.getReplicaIps(),
                        REPLICA_NUM - fileStorageDataModel.getReplicaIps().size());
                fileStorageDataModel.addReplicaIPs(newReplicaIps);
                try {
                    for (String ip : newReplicaIps) {
                        // TODO: send file
                        addReplica(ip, fileName, fileName, myIp, fileStorageDataModel);
                        // TODO: I think we need to deal with failure
                    }
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                } catch (NotBoundException exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            System.out.println("Set main invoked, but no such file stored here");
        }
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
                System.out.println(String.format("Send file to %s", replicaIp));
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
        } catch(UnknownHostException exception) {
            succeed = false;
        } catch (NotBoundException exception) {
            succeed = false;
        } catch (RemoteException exception) {
            succeed = false;
        }
    }

    private void addFileLocal(String sourceName, String targetName) {
        System.out.println("add file local invoked");
        /*
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
        */
    }

    private void delete(String targetName) {
        //Path target = Paths.get(String.format("%s/%s", FileSystemConstants.MAIN_FILE_FOLDER.getValue(), targetName));
        try {
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
                    if (changeMasterInfo(targetName, replicaIp, null)) {
                        succeed = false;
                    }
                }
            }
            succeed = true;
            //Files.delete(target);
        } catch (IOException exception) {
            exception.printStackTrace();
            succeed = false;
        } catch (NotBoundException exception) {
            exception.printStackTrace();
            succeed = false;
        }
    }

    private boolean changeMasterInfo(String name, String replicaIp, FileStorageDataModel fileStorageDataModel) {
        try {
            Registry registry = LocateRegistry.getRegistry(replicaIp,
                    Integer.parseInt(OperationConstants.LISTEN_PORT.getValue()));
            FileSystemOperationInterface operation = (FileSystemOperationInterface) registry.lookup(FileSystemOperation.class.getName());
            operation.changeMasterStorageInfoOperation(name, fileStorageDataModel);
            return true;
        } catch (RemoteException exception) {
            exception.printStackTrace();
            return false;
        } catch (NotBoundException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
