package net.balhar.ds.globalState;

import net.balhar.ds.globalState.transfer.Time;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Log implements Runnable {
    private Branch branch;
    private String logFile;
    private FileWriter fw;

    private final List<ISerializable> queue;

    public Log(Branch branch, String logFile) {
        this.branch = branch;
        this.logFile = logFile;
        this.queue = new ArrayList<ISerializable>();
    }

    @Override
    public void run() {
        try {
            fw = new FileWriter(logFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not write into the log file.");
        }

        for(;;) {
            try {
                Thread.sleep(Time.TIME_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                handleQueue();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        }
    }

    private void handleQueue() throws IOException {
        synchronized(queue) {
            for(ISerializable toLog: queue) {
                fw.write(toLog.serialize());
                fw.flush();
            }
            queue.retainAll(new ArrayList<ISerializable>());
        }
    }

    public void logGlobal(Integer actualState) {
        Global global = new Global();
        global.actualState = actualState;
        global.actuaTime = branch.getTime();
        addToQueue(global);
    }

    public void logSend(IDestination toSend) {
        if(toSend instanceof Time) {
            return;
        }

        Send send = new Send();
        send.actualTime = toSend.getTime();
        send.from = toSend.getSource();
        send.mark = toSend.getMarkInfo();
        send.sequenceNumber = toSend.getSequenceNumber();
        send.to = toSend.getDestination();
        addToQueue(send);
    }

    public void logReceived(IDestination toReceive) {
        if(toReceive instanceof Time) {
            return;
        }
		if(toReceive == null){
			return;
		}

        Received received = new Received();
        int branchTime = (branch != null && branch.getTime() != null) ? branch.getTime(): 0;
        received.actualTime = (branchTime > toReceive.getTime()) ? branchTime: toReceive.getTime()+1;
        received.dist = toReceive.getDestination();
        received.sequenceNumber = toReceive.getSequenceNumber();
        addToQueue(received);
    }

    private void addToQueue(ISerializable object){
        synchronized (queue) {
            queue.add(object);
        }
    }

    private interface ISerializable{
        String serialize();
    }

    private class Received implements ISerializable{
        Integer actualTime;
        ClientInfo dist;
        String sequenceNumber;

        @Override
        public String serialize() {
            return String.format("R&&%s&%s&%s\n", dist.serialize(), actualTime.toString(), sequenceNumber);
        }
    }

    private class Send implements ISerializable{
        Integer actualTime;
        ClientInfo from;
        ClientInfo to;
        String sequenceNumber;
        String mark;

        @Override
        public String serialize() {
            return String.format("S&%s&%s&%s&%s&%s\n", from.serialize(), to.serialize(), actualTime.toString(), mark, sequenceNumber);
        }
    }

    private class Global implements ISerializable{
        Integer actualState;
        Integer actuaTime;

        @Override
        public String serialize() {
            return String.format("%s %s\n", actuaTime.toString(), actualState.toString());
        }
    }
}
