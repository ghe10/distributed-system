package network.datamodel;

import java.io.Serializable;

/**h
 * This class guarantees that all the data models should have it's own modelClass in itself.
 * This field is used in convert back from object to related model
 */
public class BasicModel implements Serializable {
    protected Class modelClass;
    protected String ip;
    protected int port;

    BasicModel() {

    }

    BasicModel(Class modelClass, String ip, int port) {
        this.modelClass = modelClass;
        this.ip = ip;
        this.port = port;
    }
}
