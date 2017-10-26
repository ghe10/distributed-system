package network.datamodel;

import java.io.Serializable;

/**
 * This model is used to be transmitted in network
 */
public class FileObjectModel implements Serializable {
    private String ip;
    private String senderIp;
    private String port;
    private String filePath;
    private String actionType;
    private CommunicationDataModel comData;

    public FileObjectModel(String filePath, String actionType, String ip, String senderIp, String port) {
        this.filePath = filePath;
        this.actionType = actionType;
        this.ip = ip;
        this.senderIp = senderIp;
        this.port = port;
        comData = null;
    }

    public FileObjectModel(String filePath, String actionType, String ip, String senderIp,
                           String port, CommunicationDataModel comData) {
        this.filePath = filePath;
        this.actionType = actionType;
        this.ip = ip;
        this.senderIp = senderIp;
        this.port = port;
        this.comData = comData;
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

    public CommunicationDataModel getComData() {
        return comData;
    }
}
