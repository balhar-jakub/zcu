package net.balhar.zcu.ds.transaction.operations;

/**
 *
 */
public class Sleep implements IOperation {
    private Integer sleepTime;

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String getString() {
        return "SLEEP " + sleepTime;
    }
}
