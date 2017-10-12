package network.datamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Usage:
 * put ** file ** size
 * get ** file
 * remove ** file
 * append ** target file ** new file ** size
 */
public class CommunicationDataModel extends BasicModel {
    private String senderIp;
    private String action;
    private String sourceFile;
    private String targetFile;
    private String actionDestinationIp;
    private long fileSize;
    private String infoMode;
    private String info;

    public CommunicationDataModel(String senderIp, String receiverIp, String action,
                                  String sourceFile, String targetFile, int port) {
        super(CommunicationDataModel.class, receiverIp, port);
        this.senderIp = senderIp;
        this.action = action;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        infoMode = CommunicationConstants.NO_INFO.getValue();
        info = "";
    }

    public CommunicationDataModel(String senderIp, String targetIp, String actionDestinationIp, String action,
                                  String sourceFile, String targetFile, int port) {
        super(CommunicationDataModel.class, targetIp, port);
        this.senderIp = senderIp;
        this.actionDestinationIp = actionDestinationIp;
        this.action = action;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
        infoMode = CommunicationConstants.NO_INFO.getValue();
        info = "";
    }

    /** In this function, we add the replica info to this.*/
    public void addReplicaInfo(String mainReplica, HashSet<String> replicaIps) {
        replicaIps.remove(mainReplica);
        ArrayList<String> replicaList= new ArrayList<String>(replicaIps);
        if (replicaList.size() != 2) {
            System.out.println("******* Input size incorrect *******");
            return;
        }
        infoMode = CommunicationConstants.STORAGE_INFO.getValue();
        info = String.format(CommunicationConstants.STORAGE_INFO_FORMAT.getValue(),
                mainReplica, replicaList.get(0), replicaList.get(1));
    }

    public HashSet<String> getReplicaIps() {
        if (infoMode.equals(CommunicationConstants.STORAGE_INFO.getValue())) {
            return new HashSet<String>(Arrays.asList(info.split(",")));
        } else {
            return null;
        }
    }

    public String getMainReplicaIp() {
        if (infoMode.equals(CommunicationConstants.STORAGE_INFO.getValue())) {
            String replicaIps[] = info.split(",");
            return replicaIps.length > 0 ? replicaIps[0] : null;
        } else {
            return null;
        }
    }

    public String getSenderIp() {
        return senderIp;
    }

    /** We the better add sth here for command translation and separation*/
    public String getAction() {
        return action;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public String getActionDestinationIp() {
        return actionDestinationIp;
    }

    public long getFileSize() {
        return fileSize;
    }

}
