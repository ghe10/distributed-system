package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class StaticUtils {
    public static String getLocalIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
