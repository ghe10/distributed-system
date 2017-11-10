package filesystem.serializablemodels;

import java.io.Serializable;
import java.util.HashSet;

public class FileStorageDataModel implements Serializable {
    private String fileName;
    private String mainReplicaIp;
    private HashSet<String> replicaIps;

    public FileStorageDataModel(String fileName, String mainReplicaIp, HashSet<String> replicaIps) {
        this.fileName = fileName;
        this.mainReplicaIp = mainReplicaIp;
        this.replicaIps = replicaIps;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMainReplicaIp() {
        return mainReplicaIp;
    }

    public HashSet<String> getReplicaIps() {
        return replicaIps;
    }

    public boolean isNodeReplica(String ip) {
        return replicaIps.contains(ip);
    }

    public void setMainReplicaIp(String ip) {
        this.mainReplicaIp = ip;
    }

    public void setReplicaIps(HashSet<String> replicaIps) {
        this.replicaIps = replicaIps;
    }
}
