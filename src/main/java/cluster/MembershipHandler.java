package cluster;

import utils.ObservableList;

import java.util.Observer;

/**
 * This class is used to set observer, the extended version of observer will be passed in and set from
 * the higher level application which uses this cluster
 */
public class MembershipHandler {
    public static void setMembershipHandler(ObservableList<String> observableList, Observer observer) {
        observableList.addObserver(observer);
    }
}
