package LocationServer.ProxyHandler;

public class ProxyEntity {
    private String address;
    private int port;
    private int heartbeatPort;

    public ProxyEntity(String address, int port, int heartbeatPort) {
        this.address = address;
        this.port = port;
        this.heartbeatPort = heartbeatPort;
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

    @Override
    public String toString() {
        return address + ":" + port;
    }
}