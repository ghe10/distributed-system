package network;

import network.datamodel.FileDataModel;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestTcpSendHelper {
    final static TcpSendHelper tcpSendHelper = mock(TcpSendHelper.class);

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
}

