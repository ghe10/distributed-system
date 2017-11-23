package cluster;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import utils.ObservableList;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeTest {
    private ObservableList observableList;
    private ZooKeeper zooKeeper;
    private ZkNode node;
    private String localIp;

    @Before
    public void prepare() throws IOException {
        observableList = mock(ObservableList.class);
        zooKeeper = mock(ZooKeeper.class);
        node = new ZkNode(zooKeeper, observableList);
        localIp = InetAddress.getLocalHost().getHostAddress();
    }

    @Test
    public void initNodeExistMasterTest() throws KeeperException, InterruptedException, IOException {
        // mock for register()
        LinkedList<String> nodeList = new LinkedList<String>();
        doNothing().when(zooKeeper).create(
                anyString(),
                any(byte[].class),
                eq(ZooDefs.Ids.OPEN_ACL_UNSAFE),
                eq(CreateMode.PERSISTENT),
                any(AsyncCallback.StringCallback.class),
                Matchers.anyObject());
        // mock for existMaster()
        when(zooKeeper.getData(anyString(), eq(false), any(Stat.class))).thenReturn(localIp.getBytes());

        // mock for masterRegistration()
        // TODO: test the throws in this part
        when(zooKeeper.create(
                anyString(),
                any(byte[].class),
                eq(OPEN_ACL_UNSAFE),
                eq(CreateMode.EPHEMERAL))).thenReturn("");
        // mock for getZkMasterIp()
        when(zooKeeper.getData(anyString(), any(Watcher.class),any(Stat.class))).thenReturn(localIp.getBytes());
        //  mock for getZkNodeIps
        //zooKeeper.getChildren(NODE_PATH, false);
        when(zooKeeper.getChildren(anyString(), eq(false))).thenReturn(nodeList);

        when(zooKeeper.exists(anyString(), eq(true))).thenReturn(new Stat());

        node.init();
        assertEquals(nodeList, new LinkedList<String>(node.getNodeIps()));
        assertEquals(localIp, node.getMasterIp());
    }
}
