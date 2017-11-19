package filesystem.filesystemtasks;

public enum OperationConstants {
    ADD("add"),
    ADD_MAIN("add_main"),
    GET("get"),
    DELETE("delete"),
    APPEND("append"),

    LISTEN_PORT("2048");

    private String value;

    public String getValue() {
        return this.value;
    }

    OperationConstants(String value) {
        this.value = value;
    }
}
