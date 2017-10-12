package network.datamodel;

/**
 * This class specifies the
 * communication format
 * mode
 * some action and result
 */
public enum CommunicationConstants {
    // TODO: re-balance this and Constants.java
    ACK("ack"),
    SEND("send"),
    GET_FILE("get_file"), // in com data
    DELETE("delete"),
    DELETE_SUCCESS("delete_success"),
    DELETE_FAILED("delete_failed"),

    // the following part are for info
    NO_INFO("no_info"),
    STORAGE_INFO("storage_info"),
    STORAGE_INFO_FORMAT("%s,%s,%s"); // three ips, the first one is main replica

    private String value;

    public String getValue() {
        return this.value;
    }

    private CommunicationConstants(String value) {
        this.value = value;
    }
}
