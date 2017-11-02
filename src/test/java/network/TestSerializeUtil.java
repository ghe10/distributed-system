package network;


import network.datamodel.FileDataModel;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

public class TestSerializeUtil {
    // Test the seralize deserialize together
    @Test
    public void serializeDeserializeTest() {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("Buenos Aires", "Córdoba", "La Plata"));
        byte[] buffer1= SerializeUtil.serialize(list);
        Object object1 = SerializeUtil.deserialize(buffer1);
        assertEquals(object1, list);


        FileDataModel fileDataModel = new FileDataModel("", 0, "");
        byte[] buffer2 = SerializeUtil.serialize(fileDataModel);
        FileDataModel fileDataModel2 = (FileDataModel)SerializeUtil.deserialize(buffer2);
        assertEquals(fileDataModel2.getFilePath(), fileDataModel.getFilePath());
        assertEquals(fileDataModel2.getIp(), fileDataModel.getIp());
        assertEquals(fileDataModel2.getPort(), fileDataModel.getPort());
        assertEquals(fileDataModel2.getCommunicationInfo(), fileDataModel.getCommunicationInfo());
    }
}
