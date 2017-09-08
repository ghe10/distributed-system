package datamodels;

/**
 * This class is no longer used. The cluster should be maintained by the zookeeper part
 * This enum represents the types of swim ping and ack data.
 * Indirect ping is implemented but not used
 */
public enum SwimDataType {
    PING("ping"),
    INDIRECT_PING("indirect_ping"),
    ACK("ack");

    private String dataType;

    private SwimDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }
}