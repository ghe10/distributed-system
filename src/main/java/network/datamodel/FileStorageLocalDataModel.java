package network.datamodel;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

public class FileStorageLocalDataModel {
    private String fileName;
    private String mainReplicaIp;
    private long fileSize;
    private boolean isMainReplica;
    private HashSet<String> replicaIps;

    public FileStorageLocalDataModel(String fileName, String mainReplicaIp,
                              long fileSize, boolean isMainReplica, HashSet<String> replicaIps) {
        this.fileName = fileName;
        this.mainReplicaIp = mainReplicaIp;
        this.fileSize = fileSize;
        this.isMainReplica = isMainReplica;
        this.replicaIps = replicaIps;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMainReplicaIp() {
        return mainReplicaIp;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean isMainReplica(String myIp) {
        return myIp.equals(mainReplicaIp);
    }

    /* The none main replicas should update mainReplica according to some info from master */
    public void setMainReplica(String mainReplicaIp, String myIp) {
        // bad design actually
        this.mainReplicaIp = mainReplicaIp;
        if (mainReplicaIp.equals(myIp)) {
            isMainReplica = true;
        } else {
            isMainReplica = false;
        }
    }

    public void setReplicas(HashSet<String> replicaIps) {
        this.replicaIps = replicaIps;
    }

    public HashSet<String> getReplicaIps() {
        return replicaIps;
    }

    public boolean writeToFile(File file){
        // TODO: add write functions, maybe json or protbuf
        return true;
    }
}
