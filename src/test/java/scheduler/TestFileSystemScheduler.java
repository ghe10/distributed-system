package scheduler;

import network.datamodel.FileStorageLocalDataModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import usertool.Constants;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestFileSystemScheduler {

    private FileSystemScheduler scheduler = mock(FileSystemScheduler.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void scheduleFileGetTest() throws NoSuchFieldException, IllegalAccessException {
        Field mode = FileSystemScheduler.class.getDeclaredField("mode");
        mode.setAccessible(true);
        mode.set(scheduler, Constants.RANDOM.getValue());

        Hashtable<String, FileStorageLocalDataModel> fileStorageInfo = mock(Hashtable.class);
        FileStorageLocalDataModel fileStorageLocalDataModel = mock(FileStorageLocalDataModel.class);
        String result = "ip1", mockName = "mockName", testIp1 = "ip1", testIp2 = "ip2";
        HashSet<String> replicaIps = mock(HashSet.class);
        scheduler.setAllFileStorageInfo(fileStorageInfo);
        when(fileStorageInfo.containsKey(mockName)).thenReturn(true);
        when(fileStorageInfo.get(any(String.class))).thenReturn(fileStorageLocalDataModel);
        when(fileStorageLocalDataModel.getReplicaIps()).thenReturn(replicaIps);
        when(scheduler.randomSchedule(any(HashSet.class))).thenReturn(testIp1);

        assertEquals(testIp1, scheduler.scheduleFileGet(mockName));
        verify(scheduler).scheduleFileGet(mockName);
    }

/*
    @Before
    public void prepare() {

    }

    @Test
    public void scheduleFileGetTest() throws UnknownHostException {
        String result = "ip1", mockName = "mockName", testIp1 = "ip1", testIp2 = "ip2";
        Hashtable<String, FileStorageLocalDataModel> fileStorageInfo = mock(Hashtable.class);
        HashSet<String> replicaIps = mock(HashSet.class);

        FileSystemScheduler scheduler =
                new FileSystemScheduler("", 0, Constants.RANDOM.getValue(), fileStorageInfo);

        when(fileStorageInfo.containsKey(mockName)).thenReturn(true);
        when(scheduler.randomSchedule(replicaIps)).thenReturn(testIp1);

        scheduler.scheduleFileGet(mockName);
        verify(scheduler).scheduleFileGet(mockName);
    }
    */
}
