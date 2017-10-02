package network.datamodel;

/**
 * This class specifies the communication format and commands
 */
public enum CommunicationConstants {
    REQUEST("request"),
    ACK("ack");

    private String value;

    public String getValue() {
        return this.value;
    }

    private CommunicationConstants(String value) {
        this.value = value;
    }
}
