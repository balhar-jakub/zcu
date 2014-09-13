package net.balhar.ds.sequencer;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 17.10.13
 * Time: 13:07
 */
public class Mark {
    private String process;
    private String text;
    private String timestamp;

    public Mark(String process, String text, String timestamp){
        this.process = process;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getProcess() {
        return process;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String serialize(){
        return String.format("%s;%s;%s;",
                getProcess(),
                getText(),
                getTimestamp());
    }

    public static Mark deserialize(String deserializedData) {
        String[] parts = deserializedData.split(";");
        if(parts.length < 3){
            throw new RuntimeException("Wrong data from web");
        }
        return new Mark(parts[0], parts[1], parts[2]);
    }
}
