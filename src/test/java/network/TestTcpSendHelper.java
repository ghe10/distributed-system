package network;

import network.datamodel.FileDataModel;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/*
Second second = mock(Second.class)
when(second.doSecond()).thenReturn("Stubbed Second");
whenNew(Second.class).withAnyArguments.thenReturn(second);
 */
public class TestTcpSendHelper {
    final static TcpSendHelper tcpSendHelper = mock(TcpSendHelper.class);

    @Test
    public void sendBytesTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        Field field = TcpSendHelper.class.getDeclaredField("dataOutputStream");
        field.setAccessible(true);

        DataOutputStream dataOutputStream = mock(DataOutputStream.class);
        field.set(tcpSendHelper, dataOutputStream);
        doNothing().when(dataOutputStream).write(any(byte[].class));
        tcpSendHelper.sendBytes(any(byte[].class));

        verify(tcpSendHelper).sendBytes(any(byte[].class));
    }

    @Test
    public void sendObjectTest() throws IOException {
        Serializable object = mock(FileDataModel.class);
        doNothing().when(tcpSendHelper).sendBytes(any(byte[].class));

        tcpSendHelper.sendObject(object);
        tcpSendHelper.sendObject(null);
        verify(tcpSendHelper).sendObject(object);
        verify(tcpSendHelper).sendObject(null);
    }

    @Test
    public void sendObjectExceptionTest() throws IOException {
        Serializable object = mock(FileDataModel.class);
        doThrow(IOException.class).when(tcpSendHelper).sendBytes(any(byte[].class));

        tcpSendHelper.sendObject(object);
        verify(tcpSendHelper).sendObject(object);
    }

    @Test
    public void sendFileTest() throws IOException {
        String filePath = "";
        doNothing().when(tcpSendHelper).sendBytes(any(byte[].class));

        // what should be done inside? it is always exception actually
        tcpSendHelper.sendFile(filePath);
        verify(tcpSendHelper).sendFile(filePath);
    }

    @Test
    public void sendFileExceptionTest() throws IOException {
        String filePath = null;
        doNothing().when(tcpSendHelper).sendBytes(any(byte[].class));

        // what should be done inside? it is always exception actually
        tcpSendHelper.sendFile(filePath);
        verify(tcpSendHelper).sendFile(filePath);
    }

    @Test
    public void clearTest() {
        tcpSendHelper.clear();

        verify(tcpSendHelper).clear();
    }
}

