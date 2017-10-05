package network.datamodel;

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

    public CommunicationDataModel(String senderIp, String receiverIp, String action,
                                  String sourceFile, String targetFile, int port) {
        super(CommunicationDataModel.class, receiverIp, port);
        this.senderIp = senderIp;
        this.action = action;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
    }

    public CommunicationDataModel(String senderIp, String targetIp, String actionDestinationIp, String action,
                                  String sourceFile, String targetFile, int port) {
        super(CommunicationDataModel.class, targetIp, port);
        this.senderIp = senderIp;
        this.actionDestinationIp = actionDestinationIp;
        this.action = action;
        this.sourceFile = sourceFile;
        this.targetFile = targetFile;
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
