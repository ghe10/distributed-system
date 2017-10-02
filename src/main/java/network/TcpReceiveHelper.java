package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class serves as a base class of all the tcp receivers
 */
public class TcpReceiveHelper implements Runnable{
    private Boolean initSuccess;
    protected Boolean shutDown;
    protected ServerSocket serverSocket;

    public TcpReceiveHelper() {

    }

    public TcpReceiveHelper(int port) {
        try {
            serverSocket = new ServerSocket(port);
            initSuccess = true;
            shutDown = false;
        } catch (IOException exception) {
            exception.printStackTrace();
            initSuccess = false;
            shutDown = true;
        }
    }

    /* should we allocate the byetBuffer somewhere in the try ?? */
    public Object receive(int timeout) {
        byte[] byteBuffer = new byte[1024];
        if (serverSocket == null) {
            System.err.println("*********** NULL serverSocket in TcpReceiverHelper ************");
            return null;
        }
        try {
            serverSocket.setSoTimeout(timeout);
            Socket socket = serverSocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            dataInputStream.read(byteBuffer);
            Object object = SerializeUtil.deserialize(byteBuffer);
            return object;
        } catch(IOException exception) {
            System.err.println("*********** IOException in TcpReceiverHelper ************");
            return null;
        }
    }

    public boolean isInitSuccess(){
        return initSuccess;
    }

    public void shutDownHelper() {
        shutDown = true;
    }

    public void run() {}
}
