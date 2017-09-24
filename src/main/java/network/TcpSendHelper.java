package network;

import java.io.*;
import java.net.Socket;

public class TcpSendHelper {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    public Boolean initSuccess;

    public TcpSendHelper() {

    }

    public TcpSendHelper(int port, String ip) {
        try {
            socket = new Socket(ip, port);
            initSuccess = true;
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
            initSuccess = false;
        }
    }

    private void sendBytes(byte[] bytes) throws IOException{
        dataOutputStream.write(bytes);
    }

    public void sendObject(Object object) {
        if (object == null) {
            return;
        }
        byte[] bytes = SerializeUtil.serialize(object);
        try {
            sendBytes(bytes);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void sendFile(String path) {
        File file = new File(path);
        byte[] sendBuffer = new byte[1024];
        int length = 0;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            dataOutputStream.writeUTF(file.getName());
            while ((length = fileInputStream.read(sendBuffer, 0, sendBuffer.length)) > 0) {
                sendBytes(sendBuffer);
            }
            fileInputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void clear() {
        try {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
