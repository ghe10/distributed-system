package network.datamodel;

/**
 * This class specifies the communication format and commands
 */
public enum CommunicationConstants {
    ACK("ack"),
    SEND("send"),
    PUT_PRIMARY_REPLICA("put_primary_replica"),
    ADD_REPLICA("add_replica"),
    ADD_REPLICAS("add_replicas"),
    DELETE("delete");

    private String value;

    public String getValue() {
        return this.value;
    }

    private CommunicationConstants(String value) {
        this.value = value;
    }
}
