package network;

import java.io.*;
import java.net.Socket;

public class TcpFileReceiveHelper extends TcpReceiveHelper {
    private String path;
    public TcpFileReceiveHelper(int port, String path) {
        super(port);
        this.path = path;
    }

    @Override
    public void run() {
        byte[] byteBuffer = new byte[1024];
        while (!shutDown) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String fileName = dataInputStream.readUTF();
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while (dataInputStream.read(byteBuffer) > 0) {
                    fileOutputStream.write(byteBuffer);
                }
                fileOutputStream.close();
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
