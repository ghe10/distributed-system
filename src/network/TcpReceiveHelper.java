package network;

import java.io.IOException;
import java.net.ServerSocket;


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

    public Boolean isInitSuccess(){
        return initSuccess;
    }

    public void shutDownHelper() {
        shutDown = true;
    }

    public void run() {}
}
