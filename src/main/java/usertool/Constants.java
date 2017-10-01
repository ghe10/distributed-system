package usertool;

public enum Constants {
    SERVER_MODE("s"),
    WORKER_MODE("w"),
    CLIENT_MODE("c"),

    DEFAULT_CONFIG_PATH("config/configuration.cfg"),

    DEFAULT_HOST_PORT("2181"),
    FILE_RECEIVE_PORT("10000"),
    FILE_OBJECT_RECEIVE_PORT("20000"),

    DEFAULT_SESSION_TIMEOUT("15000"),
    SLEEP_INTERVAL("1000"),

    SHUT_DOWN("quit"),

    ADD_FILE("add_file"),
    REMOVE_FILE("remove_file"),
    REQUEST_FILE("request_file");

    private String value;

    public String getValue() {
        return this.value;
    }

    private Constants(String value) {
        this.value = value;
    }
}
