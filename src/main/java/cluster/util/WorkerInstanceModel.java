package cluster.util;

public class WorkerInstanceModel {
    private String ip;
    private String serverName;
    private int serverId;

    public WorkerInstanceModel(String ip, String serverName) {
        this.ip = ip;
        this.serverName = serverName;
        String[] info = serverName.split("-");
        if (info.length < 2) {
            serverId = -1;
        }
        try {
            serverId = Integer.parseInt(info[1]);
        } catch (NumberFormatException exception) {
            serverId = -1;
        }
    }

    public String getIp() {
        return ip;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerId() {
        return serverId;
    }
}
