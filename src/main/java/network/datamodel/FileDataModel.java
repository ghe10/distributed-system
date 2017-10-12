package network.datamodel;

/**
 * This data model represents a file send task, which includes : target ip, target port, file path.
 * To avoid any change to the task after task creation, the parms can't be changed
 */

// I think we should depreticate this shit
public class FileDataModel extends BasicModel {
    private String filePath;
    private CommunicationDataModel communicationInfo;

    public FileDataModel(String ip, int port, String filePath) {
        super(FileObjectModel.class, ip, port);
        this.filePath = filePath;
        communicationInfo = null;
    }

    public FileDataModel(String ip, int port, String filePath, CommunicationDataModel communicationInfo) {
        super(FileObjectModel.class, ip, port);
        this.filePath = filePath;
        this.communicationInfo = communicationInfo;
    }

    public String getIp(){
        return ip;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getPort() {
        return port;
    }

    public CommunicationDataModel getCommunicationInfo() {
        return communicationInfo;
    }
}
