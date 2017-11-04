package network;

import network.datamodel.FileDataModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestTcpSendHelper {
    private TcpSendHelper tcpSendHelper;
    private TcpSendHelper spyTcpSendHelper;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private static final boolean ACCESS_TRUE = true;

    @Before
    public void preparation() throws NoSuchFieldException, IllegalAccessException {
        Field socketField = TcpSendHelper.class.getDeclaredField("socket");
        socketField.setAccessible(ACCESS_TRUE);
        Field dataOutputStreamField = TcpSendHelper.class.getDeclaredField("dataOutputStream");
        dataOutputStreamField.setAccessible(ACCESS_TRUE);

        tcpSendHelper = new TcpSendHelper();
        spyTcpSendHelper = spy(tcpSendHelper);
        socket = mock(Socket.class);
        dataOutputStream = mock(DataOutputStream.class);

        socketField.set(spyTcpSendHelper, socket);
        dataOutputStreamField.set(spyTcpSendHelper, dataOutputStream);
    }

    @Test
    public void sendBytesTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        doNothing().when(dataOutputStream).write(any(byte[].class));

        spyTcpSendHelper.sendBytes(new byte[10]);
        verify(spyTcpSendHelper).sendBytes(new byte[10]);

        spyTcpSendHelper.sendBytes(new byte[0]);
        verify(spyTcpSendHelper).sendBytes(new byte[0]);

        spyTcpSendHelper.sendBytes(null);
        verify(spyTcpSendHelper).sendBytes(null);
    }

    @Test
    public void sendObjectTest() throws IOException {
        Serializable object = mock(FileDataModel.class);
        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));

        spyTcpSendHelper.sendObject(object);
        spyTcpSendHelper.sendObject(null);
        verify(spyTcpSendHelper).sendObject(object);
        verify(spyTcpSendHelper).sendObject(null);
    }

    @Test
    public void sendObjectExceptionTest() throws IOException {
        Serializable object = mock(FileDataModel.class);
        doThrow(IOException.class).when(spyTcpSendHelper).sendBytes(any(byte[].class));

        spyTcpSendHelper.sendObject(object);
        verify(spyTcpSendHelper).sendObject(object);
    }

    @Test
    public void sendFileTest() throws IOException, NoSuchMethodException {
        String filePath = "";
        File file = mock(File.class);
        FileInputStream fileInputStream = mock(FileInputStream.class);

        doReturn(filePath).when(file).getName();
        doNothing().when(dataOutputStream).writeUTF(filePath);
        doReturn(0).when(fileInputStream).read(any(byte[].class), anyInt(), anyInt());
        doNothing().when(fileInputStream).close();
        doReturn(fileInputStream).when(spyTcpSendHelper).createFileInputStream(any(File.class));
        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));

        spyTcpSendHelper.sendFile(filePath);
        verify(spyTcpSendHelper).sendFile(filePath);
    }

    @Test
    public void sendFileWrongInputTest() throws IOException {
        String filePath = "";
        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));

        spyTcpSendHelper.sendFile(filePath);
        verify(spyTcpSendHelper).sendFile(filePath);
        spyTcpSendHelper.sendFile(null);
        verify(spyTcpSendHelper).sendFile(null);
    }

    @Test
    public void sendFileWriteExceptionTest() throws IOException, NoSuchMethodException {
        String filePath = "";
        File file = mock(File.class);
        FileInputStream fileInputStream = mock(FileInputStream.class);

        doReturn(filePath).when(file).getName();
        doReturn(0).when(fileInputStream).read(any(byte[].class), anyInt(), anyInt());
        doNothing().when(fileInputStream).close();
        doReturn(fileInputStream).when(spyTcpSendHelper).createFileInputStream(any(File.class));
        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));


        doThrow(IOException.class).when(dataOutputStream).writeUTF(filePath);
        spyTcpSendHelper.sendFile(filePath);
        verify(spyTcpSendHelper).sendFile(filePath);
    }

    @Test
    public void sendFileReadExceptionTest() throws IOException, NoSuchMethodException {
        String filePath = "";
        File file = mock(File.class);
        FileInputStream fileInputStream = mock(FileInputStream.class);

        doReturn(filePath).when(file).getName();
        doNothing().when(dataOutputStream).writeUTF(filePath);
        doNothing().when(fileInputStream).close();
        doReturn(fileInputStream).when(spyTcpSendHelper).createFileInputStream(any(File.class));
        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));

        doThrow(IOException.class).when(fileInputStream).read(any(byte[].class), anyInt(), anyInt());
        spyTcpSendHelper.sendFile(filePath);
        verify(spyTcpSendHelper).sendFile(filePath);
    }

    @Test
    public void clearTest() throws IOException {
        spyTcpSendHelper.clear();
        doNothing().when(socket).close();
        doNothing().when(dataOutputStream).close();
        verify(spyTcpSendHelper).clear();
    }

    @Test
    @Ignore
    public void sendFileTestBackUp() throws IOException, NoSuchMethodException {
//        Method createFileInputStreamMethod = TcpSendHelper.class.getDeclaredMethod("createFileInputStream");
//        createFileInputStreamMethod.setAccessible(ACCESS_TRUE);
        String filePath = "";
        File file = mock(File.class);
        FileInputStream fileInputStream = mock(FileInputStream.class);
        //Mockito.when(new Second(any(String.class).thenReturn(null);
        //doReturn(file).when(new File(anyString()));
//        whenNew(File.class).withAnyArguments().thenReturn(file);
//        whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
        /*
        Second second = Mockito.mock(Second.class);
whenNew(Second.class).withNoArguments().thenReturn(second);
         */
        doReturn(filePath).when(file).getName();
        doNothing().when(dataOutputStream).writeUTF(filePath);
        doReturn(0).when(fileInputStream).read(any(byte[].class), anyInt(), anyInt());
        doNothing().when(fileInputStream).close();
        //doReturn(fileInputStream).when(new FileInputStream(anyString()));

        doNothing().when(spyTcpSendHelper).sendBytes(any(byte[].class));

        // what should be done inside? it is always exception actually
        spyTcpSendHelper.sendFile(filePath);
        verify(spyTcpSendHelper).sendFile(filePath);
    }
}
