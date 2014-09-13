package net.balhar.ds.sequencer;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 15.10.13
 * Time: 15:52
 */
public class Event {
    String sourceProcess;
    String sourceTime;
    String distProcess;
    String distTime;
    Operation data;

    public Event(){

    }

    public Event(String sourceProcess, String sourceTime, String distProcess, String distTime, Operation data){
        this.sourceProcess = sourceProcess;
        this.sourceTime = sourceTime;
        this.distProcess = distProcess;
        this.distTime = distTime;
        this.data = data;
    }

    public String getSourceProcess() {
        return sourceProcess;
    }

    public String getSourceTime() {
        return sourceTime;
    }

    public String getDistProcess() {
        return distProcess;
    }

    public String getDistTime() {
        return distTime;
    }

    public Operation getData() {
        return data;
    }

    public void setSourceProcess(String sourceProcess) {
        this.sourceProcess = sourceProcess;
    }

    public void setSourceTime(String sourceTime) {
        this.sourceTime = sourceTime;
    }

    public void setDistProcess(String distProcess) {
        this.distProcess = distProcess;
    }

    public void setDistTime(String distTime) {
        this.distTime = distTime;
    }

    public void setData(Operation data) {
        this.data = data;
    }

    public String serialize(){
        return String.format("%s&%s&%s&%s&%s&",
                getSourceProcess(),
                getSourceTime(),
                getDistProcess(),
                getDistTime(),
                getData().serialize());
    }

    public static Event deserialize(String receivedData) {
        String[] eventParts = receivedData.split("&");
        if(eventParts.length < 5){
            throw new RuntimeException("Wrong data from web");
        }
        String sourceProcess = eventParts[0];
        String sourceTime = eventParts[1];
        String distProcess = eventParts[2];
        String distTime = eventParts[3];
        Operation data = Operation.deserialize(eventParts[4]);
        return new Event(sourceProcess,sourceTime,distProcess,distTime,data);
    }
}
