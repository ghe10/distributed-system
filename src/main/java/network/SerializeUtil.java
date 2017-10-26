package network;

import org.apache.commons.lang3.SerializationUtils;

import java.io.*;

/**
 * This class provides static methods to convert object to byte array and convert back
 */
public class SerializeUtil {
    public static byte[] serialize(Serializable object) {
        byte[] bytes = null;
        if (object == null) {
            return null;
        }
        bytes = SerializationUtils.serialize(object);
        return bytes;
    }

    public static Object deserialize(byte[] bytes) {
        Object object = null;
        if (bytes == null) {
            return null;
        }
        object = SerializationUtils.deserialize(bytes);
        return object;
    }
}
