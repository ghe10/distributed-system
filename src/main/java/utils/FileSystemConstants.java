package utils;

public enum FileSystemConstants {
    NO_MAIN_REPLICA("no_main_replica"),
    REPLICA_NUMBER("2"),

    TEMP_FILE_FOLDER("tmp"),
    MAIN_FILE_FOLDER("main"),

    SHUT_DOWN("shut_down");

    private String value;

    public String getValue() {
        return this.value;
    }

    private FileSystemConstants(String value) {
        this.value = value;
    }
}
