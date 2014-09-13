package net.balhar.ds.globalState.transfer;

import net.balhar.ds.globalState.ClientInfo;
import net.balhar.ds.globalState.IDestination;

import java.util.UUID;

/**
 *
 */
public class Time implements IDestination {
    public static final Integer TIME_UNIT = 1;
    private Integer time;
    private ClientInfo destination;

    public Time(){}

    public Time(Integer time, ClientInfo destination) {
        this.time = time;
        this.destination = destination;
    }

    public Integer getTime() {
        return time;
    }

    @Override
    public ClientInfo getSource() {
        return null;
    }

    @Override
    public ClientInfo getDestination() {
        return destination;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSequenceNumber() {
        return null;
    }

    @Override
    public String getMarkInfo() {
        return null;
    }
}
