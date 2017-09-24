package network.datamodel;

/**
 * This model is used to be transmitted in network
 */
public class FileObjectModel {
    private String filePath;
    private String actionType;

    public FileObjectModel(String filePath, String actionType) {
        this.filePath = filePath;
        this.actionType = actionType;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getActionType() {
        return actionType;
    }
}
