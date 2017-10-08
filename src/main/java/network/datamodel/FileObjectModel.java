package network.datamodel;

import org.apache.zookeeper.AsyncCallback;

/**
 * This model is used to be transmitted in network
 */
public class FileObjectModel {
    private String ip;
    private String senderIp;
    private String port;
    private String filePath;
    private String actionType;

    public FileObjectModel(String filePath, String actionType, String ip, String senderIp, String port) {
        this.filePath = filePath;
        this.actionType = actionType;
        this.ip = ip;
        this.senderIp = senderIp;
        this.port = port;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getActionType() {
        return actionType;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getSenderIp() {
        return senderIp;
    }
}
