package usertool;

public enum Constants {
    SERVER_MODE("s"),
    WORKER_MODE("w"),
    CLIENT_MODE("c"),

    DEFAULT_CONFIG_PATH("config/configuration.cfg"),
    DEFAULT_HOST_PORT("2181"),

    DEFAULT_SESSION_TIMEOUT("15000"),

    SHUT_DOWN("quit");

    private String value;

    public String getValue() {
        return this.value;
    }

    private Constants(String value) {
        this.value = value;
    }
}
