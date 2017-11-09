package cluster;

import java.util.Observable;
import java.util.Observer;

/**
 * This class will be extended by fs, here it mainly used to test the observer part.
 */
public class BasicObserver implements Observer {
    public void update(Observable observable, Object object) {
        System.out.println(String.format("Observed object : %s", object.toString()));
    }
}
