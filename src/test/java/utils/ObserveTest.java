package utils;

import cluster.BasicObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ObserveTest {
    private ObservableList<String> observableList;

    @Before
    public void prepare() {
        LinkedList<String> linkedList = new LinkedList<String>();
        observableList = new ObservableList<String>(linkedList);
    }

    @Test
    public void observerTest() {
        BasicObserver basicObserver = mock(BasicObserver.class);
        observableList.addObserver(basicObserver);
        observableList.add("");
        verify(basicObserver).update(any(Observable.class), anyObject());
    }

    @Test
    public void multiObserverTest() {
        BasicObserver basicObserver1 = mock(BasicObserver.class);
        BasicObserver basicObserver2 = mock(BasicObserver.class);
        BasicObserver basicObserver3 = mock(BasicObserver.class);
        observableList.addObserver(basicObserver1);
        observableList.addObserver(basicObserver2);
        observableList.addObserver(basicObserver3);
        observableList.add("");
        verify(basicObserver1).update(any(Observable.class), anyObject());
        verify(basicObserver2).update(any(Observable.class), anyObject());
        verify(basicObserver3).update(any(Observable.class), anyObject());
    }
}
