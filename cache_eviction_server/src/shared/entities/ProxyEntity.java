package shared.entities;

public class ProxyEntity implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String address;
    private int port;
    private int heartbeatPort;
    private int rmiReplicaPort;

    public ProxyEntity(String address, int port, int heartbeatPort, int rmiReplicaPort) {
        this.address = address;
        this.port = port;
        this.heartbeatPort = heartbeatPort;
        this.rmiReplicaPort = rmiReplicaPort;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getHeartbeatPort() {
        return heartbeatPort;
    }

    public int getRmiReplicaPort() {
        return rmiReplicaPort;
    }

    @Override
    public String toString() {
        return address + ":" + port + " (heartbeat port: " + heartbeatPort + ")";
    }
}