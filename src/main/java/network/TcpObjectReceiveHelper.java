package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This class reads object from tcp connection, the assumption is that the object
 * is not larger than 1024 bytes.
 */
public class TcpObjectReceiveHelper extends TcpReceiveHelper {
    private LinkedList<Object> objectQueue;

    public TcpObjectReceiveHelper(int port, LinkedList<Object> objectQueue) {
        super(port);
        this.objectQueue = objectQueue;
    }

    @Override
    public void run() {
        byte[] byteBuffer = new byte[1024];
        while (!shutDown) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                dataInputStream.read(byteBuffer);
                Object object = SerializeUtil.deserialize(byteBuffer);
                synchronized (objectQueue) {
                    objectQueue.add(object);
                }
                dataInputStream.close();
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        try {
            serverSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
