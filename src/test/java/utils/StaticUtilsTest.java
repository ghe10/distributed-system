package utils;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class StaticUtilsTest {
    @Test
    public void getLocalIpTest() throws UnknownHostException {
        String myIp = InetAddress.getLocalHost().getHostAddress();
        assertEquals(myIp, StaticUtils.getLocalIp());
    }
}
