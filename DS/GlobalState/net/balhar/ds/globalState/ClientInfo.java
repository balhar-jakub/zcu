package net.balhar.ds.globalState;

/**
 *
 */
public class ClientInfo {
    private int port;

    public ClientInfo(){}

    public ClientInfo(int port, String address) {
        this.port = port;
        this.address = address;
    }

    private String address;

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String serialize() {
        return String.format("%s:%s", getAddress(), getPort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientInfo that = (ClientInfo) o;

        return port == that.port && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + address.hashCode();
        return result;
    }
}
