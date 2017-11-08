package cluster.util;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import usertool.Constants;

public class MasterUtil {
    public static String getMasterIp(ZooKeeper zooKeeper) throws InterruptedException, KeeperException {
        if (zooKeeper == null) {
            throw new NullPointerException();
        }
        Stat stat = new Stat();
        byte masterData[] = zooKeeper.getData(Constants.MASTER_PATH.getValue(), false, stat);
        System.out.println("*************** Master data ****************");
        System.out.println(new String(masterData));
        return new String(masterData);
    }

}
