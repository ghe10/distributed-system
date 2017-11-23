package filesystem.scheduler;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RandomSchedulerTest {
    private RandomScheduler randomScheduler;
    private HashSet<String> candidateSet;
    private static final String IP_1 = "ip1";
    private static final String IP_2 = "ip2";
    private static final String IP_3 = "ip3";
    private static final String IP_4 = "ip4";

    @Before
    public void prepare() {
        randomScheduler = new RandomScheduler();
        candidateSet = new HashSet<String>(Arrays.asList(IP_1, IP_2, IP_3, IP_4));
    }

    @Test
    public void randomScheduleTest() {
        HashSet<String> used = new HashSet<String>(Arrays.asList(IP_1, IP_2, IP_3));
        assertEquals(IP_4, randomScheduler.randomSchedule(candidateSet, used));

        candidateSet.removeAll(used);
        assertEquals(IP_4, randomScheduler.randomSchedule(candidateSet, null));

        assertNull(randomScheduler.randomSchedule(null, null));
        assertNull(randomScheduler.randomSchedule(candidateSet, candidateSet));
    }

    @Test
    public void randomMultiScheduleTest() {
        HashSet<String> used = new HashSet<String>(Arrays.asList(IP_1, IP_2));
        HashSet<String> result = new HashSet<String>(Arrays.asList(IP_3, IP_4));

        assertEquals(result, randomScheduler.randomSchedule(candidateSet, used, 2));

        candidateSet = new HashSet<String>(Arrays.asList(IP_3, IP_4));
        assertEquals(result, randomScheduler.randomSchedule(candidateSet, null, 2));

        assertNull(randomScheduler.randomSchedule(candidateSet, used, 4));
        assertNull(randomScheduler.randomSchedule(null, null, 1));
        assertNull(randomScheduler.randomSchedule(null, used, 1));
    }
}
