package network.datamodel;

public class CommunicationDataModel extends BasicModel {
    private String senderIp;
    private String command;
    public CommunicationDataModel(String snederIp, String targetIp, String command, int port) {
        super(CommunicationDataModel.class, targetIp, port);
        this.senderIp = senderIp;
    }

    public String getSenderIp() {
        return senderIp;
    }

    /** We the better add sth here for command translation and separation*/
    public String getCommand() {
        return command;
    }
}
