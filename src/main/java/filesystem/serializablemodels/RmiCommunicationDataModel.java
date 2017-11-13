package filesystem.serializablemodels;

import java.io.Serializable;

/**
 * This serializable is used in communication
 * The source and target file name are used in file system as following:
 * 1. sourceFile is the part of file that is transmitted to node
 * 2. target file is the part of file that is operated on (may not exist now).
 * 3. For delete, no sourceFile is provided
 *
 * So we can have :
 *
 * Add sourcefile as target file
 * Append sourcefile as target file
 * Delete target file
 * Get target file
 */
public class RmiCommunicationDataModel implements Serializable {
    private String targetFileName;
    private String sourceFileName;
    private String operation;
    private String callerIp;

    public RmiCommunicationDataModel(String targetFileName, String sourceFileName, String operation, String callerIp) {
        // caller ip is used in get file operation
        this.targetFileName = targetFileName;
        this.sourceFileName = sourceFileName;
        this.operation = operation;
        this.callerIp = callerIp;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getOperation() {
        return operation;
    }

    public String getCallerIp() {
        return callerIp;
    }
}
