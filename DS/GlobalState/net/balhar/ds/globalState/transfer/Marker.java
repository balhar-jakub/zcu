package net.balhar.ds.globalState.transfer;

import net.balhar.ds.globalState.ClientInfo;
import net.balhar.ds.globalState.IDestination;

import java.util.UUID;

/**
 *
 */
public class Marker implements IDestination {
    private ClientInfo from;
    private ClientInfo to;
    private String uuid;
    private Integer time;
    private ClientInfo initiator;

    public Marker(){}

    public Marker(ClientInfo clientInfo, String uuid, ClientInfo to, Integer time, ClientInfo initiator) {
        this.uuid = uuid;
        this.from = clientInfo;
        this.to = to;
        this.time = time;
        this.initiator = initiator;
    }

    public ClientInfo getInitiator() {
        return initiator;
    }

    @Override
    public ClientInfo getSource() {
        return from;
    }

    @Override
    public ClientInfo getDestination() {
        return to;
    }

    @Override
    public String getSequenceNumber() {
        return uuid;
    }

    @Override
    public String getMarkInfo() {
        return "M";
    }

    @Override
    public Integer getTime() {
        return time;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
