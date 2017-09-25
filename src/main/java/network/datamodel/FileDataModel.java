package network.datamodel;

/**
 * This data model represents a file send task, which includes : target ip, target port, file path.
 * To avoid any change to the task after task creation, the parms can't be changed
 */
public class FileDataModel extends BasicModel {
    private String filePath;

    public FileDataModel(String ip, int port, String filePath) {
        super(FileObjectModel.class, ip, port);
        this.filePath = filePath;
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
}
