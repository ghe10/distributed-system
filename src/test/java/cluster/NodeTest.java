package cluster;

import org.apache.zookeeper.ZooKeeper;
import org.junit.Before;
import utils.ObservableList;

import java.net.UnknownHostException;

import static org.mockito.Mockito.mock;

public class NodeTest {
    private ObservableList observableList;
    private ZooKeeper zooKeeper;
    private Node node;

    @Before
    public void prepare() throws UnknownHostException {
        observableList = mock(ObservableList.class);
        zooKeeper = mock(ZooKeeper.class);
        node = new Node(zooKeeper, observableList);
    }


}
