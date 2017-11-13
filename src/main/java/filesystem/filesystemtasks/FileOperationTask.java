package filesystem.filesystemtasks;

import cluster.ClusterNodeWrapper;
import filesystem.scheduler.RandomScheduler;
import filesystem.serializablemodels.RmiCommunicationDataModel;

/**
 * This class serves as task wrapper. Note that other RMIs may be called in this task.
 * In this version, all the operations
 */
public class FileOperationTask implements Runnable {
    private RmiCommunicationDataModel rmiCommunicationDataModel;
    private RandomScheduler scheduler;
    private ClusterNodeWrapper node;

    public FileOperationTask(RmiCommunicationDataModel rmiCommunicationDataModel,
                             RandomScheduler scheduler, ClusterNodeWrapper node) {
        this.rmiCommunicationDataModel = rmiCommunicationDataModel;
        this.scheduler = scheduler;
        this.node = node;
    }

    public void run() {
        if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD_MAIN.getValue())) {
            // TODO : this is the main replica, add operation required, a new RMI should be defined for these operations
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.ADD.getValue())) {
            // this is just a replica
        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.GET.getValue())) {

        } else if (rmiCommunicationDataModel.getOperation().equals(OperationConstants.DELETE.getValue())) {

        }
        synchronized (this) {
            // notify the waiting RMI function to return
            this.notify();
        }
    }
}
