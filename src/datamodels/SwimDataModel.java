package datamodels;

/**
 * This class is no longer used, the cluster is maintained be zookeeper
 */

public class SwimDataModel {
    private String senderIp;
    private String receiverIp;
    private String dataType;
    private Boolean pingSucceed;

    private String peerIp;
    private String peerStatus;

    public SwimDataModel(String senderIp, String receiverIp, SwimDataType dataType,
                         Boolean pingSucceed,String peerIp, String peerStatus) {
        this.senderIp = senderIp;
        this.receiverIp = receiverIp;
        this.dataType = dataType.getDataType();
        this.pingSucceed = pingSucceed;

        this.peerIp = peerIp;
        this.peerStatus = peerStatus;
    }

    public SwimDataModel(String senderIp, String receiverIp, SwimDataType dataType,
                         Boolean pingSucceed) {
        this.senderIp = senderIp;
        this.receiverIp = receiverIp;
        this.dataType = dataType.getDataType();
        this.pingSucceed = pingSucceed;

        this.peerIp = null;
        this.peerStatus = null;
    }

    public String getSenderIp() {
        return this.senderIp;
    }

    public String getReceiverIp() {
        return this.receiverIp;
    }

    public String getDataType() {
        return this.dataType;
    }

    public Boolean getPingSucceed() {
        return this.pingSucceed;
    }

    public void setPeerIp(String peerIp) {
        this.peerIp = peerIp;
    }

    public void setPeerStatus(String peerStatus) {
        this.peerStatus = peerStatus;
    }

    public SwimDataModel getAckSwimDataModel(SwimDataModel swimDataModel) {
        SwimDataModel ackSwimDataModel = new SwimDataModel(
                swimDataModel.getReceiverIp(),
                swimDataModel.getSenderIp(),
                SwimDataType.ACK,
                true
        );
        return ackSwimDataModel;
    }
}

