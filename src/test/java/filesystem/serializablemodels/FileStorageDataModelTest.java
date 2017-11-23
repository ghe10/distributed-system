package filesystem.serializablemodels;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class FileStorageDataModelTest {
    private FileStorageDataModel fileStorageDataModel;
    private HashSet<String> replicaIps;
    private static final String FILE_NAME = "name";
    private static final String MAIN_REPLICA_IP = "ip";
    private static final String NEW_MAIN_REPLICA_IP = "new_ip";

    @Before
    public void preparation() {
        replicaIps = new HashSet<String>();
        fileStorageDataModel = new FileStorageDataModel(FILE_NAME, MAIN_REPLICA_IP, replicaIps);
    }

    @Test
    public void getTest() {
        assertEquals(FILE_NAME, fileStorageDataModel.getFileName());
        assertEquals(MAIN_REPLICA_IP, fileStorageDataModel.getMainReplicaIp());
        assertEquals(replicaIps, fileStorageDataModel.getReplicaIps());
    }

    @Test
    public void setTest() {
        fileStorageDataModel.setMainReplicaIp(NEW_MAIN_REPLICA_IP);
        assertEquals(NEW_MAIN_REPLICA_IP, fileStorageDataModel.getMainReplicaIp());

        replicaIps = new HashSet<String>(Arrays.asList(MAIN_REPLICA_IP, NEW_MAIN_REPLICA_IP));
        fileStorageDataModel.setReplicaIps(replicaIps);
        assertEquals(replicaIps, fileStorageDataModel.getReplicaIps());
    }
}
