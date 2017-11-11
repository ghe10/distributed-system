package filesystem.scheduler;

import cluster.Node;
import filesystem.serializablemodels.FileStorageDataModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

public class RandomScheduler {
    private Node node;
    private Hashtable<String, FileStorageDataModel> storageInfo;
    private Random random;

    public RandomScheduler(Node node, Hashtable<String, FileStorageDataModel> storageInfo) {
        this.node = node;
        this.storageInfo = storageInfo;
        random = new Random();
    }

    public String randomSchedule(HashSet<String> candidates, HashSet<String> used) {
        if (used != null) {
            candidates.removeAll(used);
        }
        if (candidates.size() <= 0) {
            return null;
        }
        ArrayList<String> candidateList = new ArrayList<String>(candidates);
        int length = candidateList.size();
        return  candidateList.get(random.nextInt(length));
    }
}
