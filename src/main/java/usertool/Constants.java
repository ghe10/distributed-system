package usertool;

public enum Constants {
    SERVER_MODE("s"),
    WORKER_MODE("w"),
    CLIENT_MODE("c"),

    DEFAULT_CONFIG_PATH("config/configuration.cfg"),

    DEFAULT_HOST_PORT("2181"),
    FILE_RECEIVE_PORT("10000"),
    FILE_OBJECT_RECEIVE_PORT("20000"),

    /* The following ports are for listen */
    CLIENT_COMMUNICATION_PORT("21000"),
    MASTER_COMMUNICATION_PORT("22000"),

    DEFAULT_SESSION_TIMEOUT("15000"),
    PUT_TIME_OUT("1200000"), // 120s
    SLEEP_INTERVAL("1000"),
    GET_MASTER_RETRY("3"),

    SHUT_DOWN("quit"),

    ADD_FILE("add_file"),
    REMOVE_FILE("remove_file"),
    REQUEST_FILE("request_file"),
    ADD_FILE_ACK("add_file_ack"),

    PUT_REFUSED_IP("refused"),

    FS_ROOT_PATH("./"),

    MASTER_PATH("/master"),
    WORKER_PATH("/workers"),
    TASK_PATH("/task"),
    ASSIGN_PATH("/assign"),
    STATUS_PATH("/status"),

    /*FS scheduler modes*/
    RANDOM("random"),

    REPLICATION_NUM("2");




    private String value;

    public String getValue() {
        return this.value;
    }

    private Constants(String value) {
        this.value = value;
    }
}
