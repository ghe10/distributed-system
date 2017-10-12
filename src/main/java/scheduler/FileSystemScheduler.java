package scheduler;

import cluster.instance.BasicWatcher;
import cluster.instance.Worker;
import cluster.util.WorkerReceiver;
import cluster.util.WorkerSender;
import network.datamodel.FileStorageLocalDataModel;
import org.apache.zookeeper.KeeperException;
import usertool.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

/**
 * Why do we have a stupid interface for scheduler with port string and session timeout int......
 */

public class FileSystemScheduler extends BasicWatcher {
    //private WorkerSender workerSender;
    //private WorkerReceiver workerReceiver;
    private String mode;
    private Random random;
    private String myIp;
    private Hashtable<String, FileStorageLocalDataModel> fileStorageInfo;

    public FileSystemScheduler(String hostPort, int sessionTimeOut,
                  WorkerSender workerSender, WorkerReceiver workerReceiver) throws UnknownHostException {
        super(hostPort, sessionTimeOut);
        //this.workerSender = workerSender;
        //this.workerReceiver = workerReceiver;
        this.mode = Constants.RANDOM.getValue();
        random = new Random();
        myIp = InetAddress.getLocalHost().getHostAddress();
        fileStorageInfo = null;
    }

    public FileSystemScheduler(String hostPort, int sessionTimeOut, WorkerSender workerSender,
                               WorkerReceiver workerReceiver, String mode) throws UnknownHostException {
        super(hostPort, sessionTimeOut);
        //this.workerSender = workerSender;
        //this.workerReceiver = workerReceiver;
        this.mode = mode;
        random = new Random();
        myIp = InetAddress.getLocalHost().getHostAddress();
        fileStorageInfo = null;
    }

    public FileSystemScheduler(String hostPort, int sessionTimeOut, String mode,
                               Hashtable<String, FileStorageLocalDataModel> fileStorageInfo)
            throws UnknownHostException {
        super(hostPort, sessionTimeOut);
        this.mode = mode;
        random = new Random();
        myIp = InetAddress.getLocalHost().getHostAddress();
        this.fileStorageInfo = fileStorageInfo;
    }

    private HashSet<String> getWorkerIp() {
        HashSet<String> workerIps = new HashSet<String>();
        try {
            List<String> workers = zooKeeper.getChildren(Constants.WORKER_PATH.getValue(), false);
            for (String worker : workers) {
                byte[] data = zooKeeper.getData(String.format("%s/%s", Constants.WORKER_PATH.getValue(), worker),
                        null, null);
                workerIps.add(new String(data));
            }
            return workerIps;
        } catch (InterruptedException exception) {
            return null;
        } catch (KeeperException exception) {
            return null;
        }
    }

    private String randomSchedule(HashSet<String> candidates) {
        Iterator<String> iterator = candidates.iterator();
        int size = candidates.size();
        int index = random.nextInt() % size;
        for (int i = 0; i < index - 1; i++){
            iterator.next();
        }
        return iterator.next();
    }

    public String scheduleMainReplica(long fileSize) {
        HashSet<String> workerIps = getWorkerIp();
        workerIps.remove(myIp);
        if (mode.equals(Constants.RANDOM.getValue())) {
            return randomSchedule(workerIps);
        }
        return null;
    }

    public ArrayList<String> scheduleFile(long fileSize, HashSet<String> existingReplicaIps, int num) {
        HashSet<String> workerIps = getWorkerIp();
        if (existingReplicaIps != null) {
            workerIps.removeAll(existingReplicaIps);
        }
        // we should avoid add self as a new replica
        workerIps.remove(myIp);
        ArrayList<String> replicaIps = new ArrayList<String>();
        String replicaIp = "";
        if (num > workerIps.size()) {
            return null;
        } else if (mode.equals(Constants.RANDOM.getValue())) {
            // random scheduling
            for (int i = 0; i < num; i++) {
                replicaIp = randomSchedule(workerIps);
                workerIps.remove(replicaIp);
                replicaIps.add(replicaIp);
            }
            return replicaIps;
        } else {
            return null;
        }
    }

    public String scheduleFileGet(String fileName) {
        FileStorageLocalDataModel thisFileStorageInfo = fileStorageInfo.getOrDefault(fileName, null);
        if (thisFileStorageInfo == null) {
            return null;
        }
        if (mode.equals(Constants.RANDOM.getValue())) {
            return randomSchedule(thisFileStorageInfo.getReplicaIps());
        }
        return null;
    }

}
