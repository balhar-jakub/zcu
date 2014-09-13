package net.balhar.ds.sequencer.comm;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 30.9.13
 * Time: 21:44
 */
public class Client {
    private String addressName;
    private int port;

    public Client(String addressName, int port){
        this.addressName = addressName;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getAddressName() {
        return addressName;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Client)) {
            return false;
        }

        Client other = (Client) obj;
        if(other.getPort() == getPort() &&
                other.getAddressName().equals(getAddressName())) {
            return true;
        }
        return false;
    }
}
