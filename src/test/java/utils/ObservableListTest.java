package utils;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObservableListTest {
    private ObservableList<String> observableList;
    private LinkedList<String> linkedList;
    private static final int ROUND = 10;

    @Before
    public void prepare() {
        linkedList = new LinkedList<String>();
        observableList = new ObservableList<String>(linkedList);
    }

    @Test
    public void addTest() {
        assertEquals(0, linkedList.size());
        for (int i = 0; i < ROUND; i++) {
            assertEquals(true, observableList.add(Integer.toString(i)));
        }
        assertEquals(ROUND, linkedList.size());
    }

    @Test
    public void getTest() {
        assertEquals(0, linkedList.size());
        for (int i = 0; i < ROUND; i++) {
            observableList.add(Integer.toString(i));
        }
        for (int i = 0; i < ROUND; i++) {
            assertEquals(linkedList.get(i), observableList.get(i));
        }
        assertNull(observableList.get(ROUND + 1));
        assertNull(observableList.get(-1));
    }

    @Test
    public void removeTest() {
        assertEquals(0, linkedList.size());
        for (int i = 0; i < ROUND; i++) {
            observableList.add(Integer.toString(i));
        }
        for (int i = ROUND - 1; i >= 0; i--) {
            assertEquals(linkedList.get(i), observableList.remove(i));
        }
        assertNull(observableList.remove(ROUND + 1));
        assertNull(observableList.remove(-1));
    }

    @Test
    public void sizeTest() {
        assertEquals(observableList.size(), linkedList.size());
        for (int i = 0; i < ROUND; i++) {
            observableList.add(Integer.toString(i));
            assertEquals(linkedList.size(), observableList.size());
        }
        for (int i = ROUND - 1; i >= 0; i--) {
            observableList.remove(i);
            assertEquals(linkedList.size(), observableList.size());
        }
        assertEquals(0, observableList.size());
    }
}
