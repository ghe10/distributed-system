package scheduler;

import network.datamodel.FileStorageLocalDataModel;
import org.junit.Before;
import org.junit.Test;
import usertool.Constants;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * We can use Mockito.CALLS_REAL_METHODS to force real function call in test.
 * We need to mock for all the possible actions
 */

public class TestFileSystemScheduler {

    private FileSystemScheduler scheduler;
    private FileSystemScheduler spyScheduler;
    private Hashtable<String, FileStorageLocalDataModel> fileStorageInfo;
    @Before
    public void preparation() throws NoSuchFieldException, IllegalAccessException {
        fileStorageInfo = mock(Hashtable.class);
        scheduler = new FileSystemScheduler();
        scheduler.setAllFileStorageInfo(fileStorageInfo);
        spyScheduler = spy(scheduler);
    }

    @Test
    public void setAllFileStorageInfoTest() throws NoSuchFieldException, IllegalAccessException {
        Hashtable<String, FileStorageLocalDataModel> fileStorageInfo = new Hashtable<>();
        spyScheduler.setAllFileStorageInfo(fileStorageInfo);
        verify(spyScheduler).setAllFileStorageInfo(fileStorageInfo);
    }


    @Test
    public void scheduleMainReplicaTest() {
        // do return in spy won't call that function
        String testIp = "ip";
        long fileSize = 0L;
        HashSet<String> ips = new HashSet<>();
        doReturn(testIp).when(spyScheduler).randomSchedule(any(HashSet.class));
        doReturn(ips).when(spyScheduler).getWorkerIp();
        assertEquals(testIp, spyScheduler.scheduleMainReplica(fileSize));
    }

    @Test
    public void scheduleMainReplicaNullTest() {
        String testIp = null;
        long fileSize = 0L;
        doReturn(testIp).when(spyScheduler).randomSchedule(any(HashSet.class));
        doReturn(null).when(spyScheduler).getWorkerIp();
        assertEquals(null, spyScheduler.scheduleMainReplica(fileSize));
    }

    @Test
    public void scheduleFileGetTest() {
        FileStorageLocalDataModel fileStorageLocalDataModel = mock(FileStorageLocalDataModel.class);
        String mockName = "mockName", falseName = "falseName", testIp1 = "ip1";
        HashSet<String> replicaIps = mock(HashSet.class);
        when(fileStorageInfo.containsKey(mockName)).thenReturn(true);
        when(fileStorageInfo.get(any(String.class))).thenReturn(fileStorageLocalDataModel);
        when(fileStorageLocalDataModel.getReplicaIps()).thenReturn(replicaIps);
        when(spyScheduler.randomSchedule(any(HashSet.class))).thenReturn(testIp1);

        assertEquals(testIp1, spyScheduler.scheduleFileGet(mockName));
        assertNull(spyScheduler.scheduleFileGet(falseName));
    }
}
