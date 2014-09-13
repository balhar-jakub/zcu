package net.balhar.zcu.ds.transaction.comm;

/**
 *  This class contains information about actually mainly server, as server does not need to know anything about its
 *  clients.
 */
public class ServerBundle {
    private String address;
    private Integer port;

    public ServerBundle(String address, Integer port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }
}
