package net.balhar.ds.globalState.transfer;

import net.balhar.ds.globalState.ClientInfo;
import net.balhar.ds.globalState.IDestination;

import java.util.UUID;

/**
 *
 */
public class Operation implements IDestination{
    private Integer sentAmount;
    private Boolean snapshotStarted;
    private ClientInfo from;
    private String uuid;
    private ClientInfo to;
    private Integer time;

    public Operation(){}

    public Operation(Integer amountToSend, ClientInfo clientInfo, boolean snapshotStarted, String uuid, ClientInfo to, Integer time) {
        this.sentAmount = amountToSend;
        this.from = clientInfo;
        this.snapshotStarted = snapshotStarted;
        this.uuid = uuid;
        this.to = to;
        this.time = time;
    }

    public Integer getSentAmount() {
        return sentAmount;
    }

    public boolean getSnapshotStarted() {
        return snapshotStarted;
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
        return getSentAmount().toString();
    }

    @Override
    public Integer getTime() {
        return time;
    }
}
