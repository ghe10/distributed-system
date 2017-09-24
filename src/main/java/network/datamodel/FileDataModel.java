package network.datamodel;

/**
 * This data model represents a file send task, which includes : target ip, target port, file path.
 * To avoid any change to the task after task creation, the parms can't be changed
 */
public class FileDataModel {
    private String ip;
    private String filePath;
    private int port;

    public FileDataModel(String ip, int port, String filePath) {
        this.ip = ip;
        this.port = port;
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
