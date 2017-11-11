package filesystem.scheduler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RandomScheduler {
    private Random random;

    public RandomScheduler() {
        random = new Random();
    }

    private String randomCandidate(HashSet<String> candidates) {
        ArrayList<String> candidateList = new ArrayList<String>(candidates);
        int length = candidateList.size();
        return  candidateList.get(random.nextInt(length));
    }

    public String randomSchedule(HashSet<String> candidates, HashSet<String> used) {
        if (candidates == null) {
            return null;
        }
        if (used != null) {
            candidates.removeAll(used);
        }
        if (candidates.size() == 0) {
            return null;
        }
        return randomCandidate(candidates);
    }

    public HashSet<String> randomSchedule(HashSet<String> candidates, HashSet<String> used,
                                          int requestedNodes) {
        HashSet<String> results = new HashSet<String>();
        if (candidates == null) {
            return null;
        }
        if (used != null) {
            candidates.removeAll(used);
        }
        if (candidates.size() < requestedNodes) {
            return null;
        }
        for (int i = 0; i < requestedNodes; i++) {
            String nodeIp = randomCandidate(candidates);
            candidates.remove(nodeIp);
            results.add(nodeIp);
        }
        return results;
    }
}
