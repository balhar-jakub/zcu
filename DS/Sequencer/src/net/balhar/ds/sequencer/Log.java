package net.balhar.ds.sequencer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 14.10.13
 * Time: 12:37
 */
public class Log implements Runnable {
    private List<Event> events;
    private List<Mark> marks;

    private ExecutorService executorService;
    private DatagramSocket serverMarkSocket;
    private DatagramSocket serverEventSocket;

    private boolean marksFinished = false;
    private boolean eventsFinished = false;

    public Log(int eventPort, int markPort){
        events = new ArrayList<Event>();
        marks = new ArrayList<Mark>();

        executorService = Executors.newFixedThreadPool(10);
        try {
            serverMarkSocket = new DatagramSocket(markPort);
            serverMarkSocket.setSoTimeout(Request.TIMEOUT);

            serverEventSocket = new DatagramSocket(eventPort);
            serverEventSocket.setSoTimeout(Request.TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("Socket was not connected");
        }
    }

    @Override
    public void run() {
        new Thread(new MarkServer()).start();
        new Thread(new EventServer()).start();

        waitFor();
    }

    private void waitFor() {
        for(;;){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(marksFinished && eventsFinished){
                executorService.shutdown();
                while(!executorService.isTerminated()){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                serialize();
                return;
            }
        }
    }

    private void serialize() {
        File logFile = new File("log");
        if(logFile.exists()){
            try {
                logFile.delete();
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String result = saveHead();
        result += saveEvents();
        result += saveMarks();
        try {
            FileWriter fw = new FileWriter(logFile);
            fw.write(result);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String saveHead() {
        List<String> processes = new ArrayList<String>();
        for(Event event : events) {
            if(!processes.contains(event.getSourceProcess())){
                processes.add(event.getSourceProcess());
            }
        }
        String processArray = "";
        for(String process: processes){
            processArray += process + ",";
        }
        if(events.size() > 0){
            processArray = processArray.substring(0, processArray.length()-1);
        }
        return String.format("config:\n" +
                "  PROCESS_FONT_SIZE: 50 # pt\n" +
                "  DATA_LINE_WIDTH: 0.01 # cm\n" +
                "processes: [%s]\n" +
                "traffic:\n", processArray);
    }

    private String saveEvents() {
        String result = "";
        String color;
        for(Event event: events) {
            if(event.getSourceProcess().equals("sequencer")) {
                if(event.getDistProcess().equals("bank1")){
                    color = "#0000ff";
                } else if(event.getDistProcess().equals("bank2")){
                    color = "#00ff00";
                } else {
                    color = "#000000";
                }
            } else if(event.getSourceProcess().equals("bank1")){
                color = "#0000ff";
            } else if(event.getSourceProcess().equals("bank2")){
                color = "#00ff00";
            } else {
                color = "#000000";
            }
            result += String.format(
                    "  - src: {p: %s, ts: %s}\n" +
                    "    dst: {p: %s, ts: %s}\n" +
                    "    data: %s\n" +
                    "    color: '%s'\n",
                    event.getSourceProcess(),
                    event.getSourceTime(),
                    event.getDistProcess(),
                    event.getDistTime(),
                    String.valueOf(event.getData().getType()) + event.getData().getAmount(),
                    color
            );
        }
        return result;
    }

    private String saveMarks(){
        String result = "marks:\n";
        String color;
        for(Mark mark: marks){
            if(mark.getProcess().equals("sequencer")){
                color = "#ff0000";
            }  else if(mark.getProcess().equals("bank1")) {
                color = "#0000ff";
            }  else if(mark.getProcess().equals("bank2")) {
                color = "#00ff00";
            }  else {
                color = "#000000";
            }
            result += String.format("- {color: '%s', p: %s, text: '%s', ts: %s}\n",
                    color,
                    mark.getProcess(),
                    mark.getText(),
                    mark.getTimestamp());
        }
        return result;
    }

    private class MarkServer implements Runnable{
        @Override
        public void run() {
            final DatagramPacket packet = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            for(;;){
                try {
                    serverMarkSocket.receive(packet);
                    executorService.execute(new JobMark(new String(packet.getData())));
                } catch (SocketTimeoutException ex) {
                    marksFinished = true;
                    serverMarkSocket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class EventServer implements Runnable{
        @Override
        public void run() {
            final DatagramPacket packet = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            for(;;){
                try {
                    serverEventSocket.receive(packet);
                    executorService.execute(new JobEvent(new String(packet.getData())));
                } catch (SocketTimeoutException ex) {
                    eventsFinished = true;
                    serverEventSocket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class JobMark implements Runnable {
        private String data;

        JobMark(String data){
            this.data = data;
        }

        @Override
        public void run() {
            System.out.println("Log Mark received: " + data);
            Mark mark = Mark.deserialize(data);
            marks.add(mark);
        }
    }

    private class JobEvent implements Runnable {
        private String data;

        JobEvent(String data){
            this.data = data;
        }

        @Override
        public void run() {
            System.out.println("Log Event received: " + data);
            Event event = Event.deserialize(data);
            events.add(event);
        }
    }

    public static void main(String[] args){
        if(args.length > 1) {
            Log log = new Log(Integer.parseInt(args[1]),Integer.parseInt(args[0]));

            new Thread(log).start();
        }
    }
}
