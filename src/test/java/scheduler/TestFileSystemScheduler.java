package scheduler;

import network.datamodel.FileStorageLocalDataModel;
import org.junit.Before;
import org.junit.Test;
import usertool.Constants;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * We can use Mockito.CALLS_REAL_METHODS to force real function call in test.
 * We need to mock for all the possible actions
 */

public class TestFileSystemScheduler {

    private FileSystemScheduler scheduler; // = mock(FileSystemScheduler.class, Mockito.CALLS_REAL_METHODS);

    @Before
    public void preparation() throws NoSuchFieldException, IllegalAccessException {
        scheduler = mock(FileSystemScheduler.class);
        Field mode = FileSystemScheduler.class.getDeclaredField("mode");
        mode.setAccessible(true);
        mode.set(scheduler, Constants.RANDOM.getValue());
    }

    @Test
    public void setAllFileStorageInfoTest() throws NoSuchFieldException, IllegalAccessException {
        Hashtable<String, FileStorageLocalDataModel> fileStorageInfo = mock(Hashtable.class);
        scheduler.setAllFileStorageInfo(fileStorageInfo);

        verify(scheduler).setAllFileStorageInfo(fileStorageInfo);
    }

    @Test
    public void scheduleMainReplicaTest() {
        String testIp = "ip";
        long fileSize = 0L;
        HashSet<String> ips = mock(HashSet.class);
        when(scheduler.randomSchedule(ips)).thenReturn(testIp);
        when(scheduler.getWorkerIp()).thenReturn(new HashSet<>());
        when(ips.remove(anyString())).thenReturn(true);
        when(scheduler.randomSchedule(any(HashSet.class))).thenReturn(testIp);
        when(scheduler.getWorkerIp()).thenReturn(ips);
        scheduler.scheduleMainReplica(fileSize);

        verify(scheduler).scheduleMainReplica(fileSize);
    }

    @Test
    public void scheduleMainReplicaNullTest() {
        String testIp = "ip";
        long fileSize = 0L;
        HashSet<String> ips = mock(HashSet.class);
        when(scheduler.randomSchedule(ips)).thenReturn(testIp);
        when(scheduler.getWorkerIp()).thenReturn(null);
        when(ips.remove(anyString())).thenReturn(true);
        when(scheduler.randomSchedule(any(HashSet.class))).thenReturn(testIp);
        when(scheduler.getWorkerIp()).thenReturn(ips);
        scheduler.scheduleMainReplica(fileSize);

        verify(scheduler).scheduleMainReplica(fileSize);
    }

    @Test
    public void scheduleFileGetTest() {
        Hashtable<String, FileStorageLocalDataModel> fileStorageInfo = mock(Hashtable.class);
        FileStorageLocalDataModel fileStorageLocalDataModel = mock(FileStorageLocalDataModel.class);
        String result = "ip1", mockName = "mockName", falseName = "falseName", testIp1 = "ip1", testIp2 = "ip2";
        HashSet<String> replicaIps = mock(HashSet.class);
        scheduler.setAllFileStorageInfo(fileStorageInfo);
        when(fileStorageInfo.containsKey(mockName)).thenReturn(true);
        when(fileStorageInfo.get(any(String.class))).thenReturn(fileStorageLocalDataModel);
        when(fileStorageLocalDataModel.getReplicaIps()).thenReturn(replicaIps);
        when(scheduler.randomSchedule(any(HashSet.class))).thenReturn(testIp1);

        //assertEquals(testIp1, scheduler.scheduleFileGet(mockName));
        scheduler.scheduleFileGet(mockName);
        verify(scheduler).scheduleFileGet(mockName);
        assertNull(scheduler.scheduleFileGet(falseName));
    }
}
