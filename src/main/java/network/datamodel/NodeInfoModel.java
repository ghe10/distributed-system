package network.datamodel;

import java.io.Serializable;

public class NodeInfoModel implements Serializable {
    private String ip;

    public NodeInfoModel(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
}
