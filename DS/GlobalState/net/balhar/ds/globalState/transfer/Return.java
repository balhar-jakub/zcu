package net.balhar.ds.globalState.transfer;

import net.balhar.ds.globalState.ClientInfo;
import net.balhar.ds.globalState.IDestination;

/**
 *
 */
public class Return implements IDestination {
    private ClientInfo from;
    private Integer state;
    private String uuid;
    private ClientInfo to;
    private Integer time;

    public Return(){}

    public Return(Integer stateOfSnapshot, String uuid, ClientInfo to, ClientInfo from, Integer time) {
        this.state = stateOfSnapshot;
        this.uuid = uuid;
        this.to = to;
        this.from = from;
        this.time = time;
    }

    public Integer getState() {
        return state;
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
        return "R " + getState().toString();
    }

    @Override
    public Integer getTime() {
        return time;
    }
}
