package datamodels;

/**
 * This class is no longer used, the cluster is maintained by zookeeper
 */

public enum PeerStatusType {
    ACTIVE("active"),
    FAILED("failed");

    private String peerStatus;

    private PeerStatusType(String peerStatus) {
        this.peerStatus = peerStatus;
    }

    public String getPeerStatus() {
        return peerStatus;
    }
}