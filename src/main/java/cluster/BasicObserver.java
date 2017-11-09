package cluster;

import java.util.Observable;
import java.util.Observer;

/**
 * This class will be extended by fs
 */
public class BasicObserver implements Observer {
    public void update(Observable observable, Object object) {
        System.out.println(String.format("Observed object : %s", object.toString()));
    }
}
