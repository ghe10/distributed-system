package utils;

public enum FileSystemConstants {
    NO_MAIN_REPLICA("no_main_replica");

    private String value;

    public String getValue() {
        return this.value;
    }

    private FileSystemConstants(String value) {
        this.value = value;
    }
}
