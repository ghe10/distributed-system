package filesystem.filesystemtasks;

import filesystem.serializablemodels.RmiCommunicationDataModel;

public class FileOperationTask implements Runnable {
    public FileOperationTask(RmiCommunicationDataModel rmiCommunicationDataModel) {

    }

    public void run() {
        // TODO: task operations
        synchronized (this) {
            // notify the waiting RMI function to return
            this.notify();
        }
    }
}
