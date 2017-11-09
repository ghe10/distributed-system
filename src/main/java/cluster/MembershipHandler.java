package cluster;

import utils.ObservableList;

public class MembershipHandler {
    public static void setMembershipHandler(ObservableList<String> observableList, BasicObserver observer) {
        observableList.addObserver(observer);
    }
}
