package net.balhar.ds.globalState;

import java.util.UUID;

/**
 *
 */
public interface IDestination {
    public ClientInfo getSource();
    public ClientInfo getDestination();
    public String getSequenceNumber();
    public String getMarkInfo();
    public Integer getTime();
}
