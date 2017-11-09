package utils;

import java.util.LinkedList;
import java.util.Observable;

/**
 * This class should be extended by fs
 */
public class ObservableList<T> extends Observable {
    private LinkedList<T> deadEventQueue;

    public ObservableList(LinkedList<T> deadEventQueue) {
        if (deadEventQueue == null) {
            throw new NullPointerException();
        }
        this.deadEventQueue = deadEventQueue;
    }

    public boolean add(T data) {
        boolean result;
        synchronized (deadEventQueue) {
            result = deadEventQueue.add(data);
            setChanged();
            notifyObservers();
        }
        return result;
    }

    public T get(int index) {
        synchronized (deadEventQueue) {
            if (index < deadEventQueue.size() && index >= 0) {
                return deadEventQueue.get(index);
            } else {
                return null;
            }
        }
    }

    public T remove(int index) {
        synchronized (deadEventQueue) {
            if (index < deadEventQueue.size() && index >= 0) {
                return deadEventQueue.remove(index);
            }
        }
        return null;
    }

    public int size() {
        synchronized (deadEventQueue) {
            return deadEventQueue.size();
        }
    }
}
