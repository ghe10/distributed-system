package network;

import java.io.*;
import java.net.Socket;

public class TcpSendHelper {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private Boolean initSuccess;

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

    void sendBytes(byte[] bytes) throws IOException {
        dataOutputStream.write(bytes);
    }

    public void sendObject(Serializable object) {
        if (object == null) {
            return;
        }
        byte[] bytes = SerializeUtil.serialize(object);
        try {
            sendBytes(bytes);
        } catch (IOException exception) {
            System.out.println("sendObject got an exception");
            exception.printStackTrace();
        }
    }

    public boolean isInitSuccess() {
        return initSuccess;
    }

    FileInputStream createFileInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException exception) {
            return null;
        }
    }

    public void sendFile(String path) {
        byte[] sendBuffer = new byte[1024];
        if (path == null) {
            // according to our test, path == null is not handled by new File
            return;
        }
        try {
            File file = new File(path);
            FileInputStream fileInputStream = createFileInputStream(file);
            if (fileInputStream == null)
                return;
            dataOutputStream.writeUTF(file.getName());
            while (fileInputStream.read(sendBuffer, 0, sendBuffer.length) > 0) {
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
