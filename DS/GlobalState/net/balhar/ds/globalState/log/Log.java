package net.balhar.ds.globalState.log;

import java.io.*;
import java.util.*;

/**
 *
 */
public class Log {
    private List<File> file;
    private File output;
    private String[] processes;
    private String[] colors;
    private List<String> received;
    private List<String> sent;

    public Log(String[] filePath, String fileOutputPath, String... processes) {
        this.file = new ArrayList<File>();
        for(String file: filePath){
            this.file.add(new File(file));
        }
        output = new File(fileOutputPath);
        this.processes = processes;
        for(int i = 0; i < processes.length; i++){
            processes[i] = processes[i].replaceAll(":","-");
        }
        colors = new String[]{"#FF0000","#0000FF","#00FF00","#000000","#FF00FF"};
        received = new ArrayList<String>();
        sent = new ArrayList<String>();
    }

    public void write() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        String head = saveHead(processes);
        String marks = "marks:\n";
        String events = "traffic:\n";
        for(String process: processes){
            marks += saveMark("black", process, "1000", "0");
        }

        for(int i = 0; i < this.file.size(); i++){
            marks += workOneFile(this.file.get(i), i);
        }
        // Najít spojení received and send.
        Map<String,ConnectedSendReceiveBox> connected = new HashMap<String,ConnectedSendReceiveBox>();
        for(String receivedLine: received){
            String seqNumber = receivedLine.split("&")[4];
            ConnectedSendReceiveBox box = connected.get(seqNumber);
            if(box == null){
                box = new ConnectedSendReceiveBox();
                box.setReceived(receivedLine);
                connected.put(seqNumber, box);
            } else {
                box.setReceived(receivedLine);
            }
        }

        for(String sendLine: sent){
            String seqNumber = sendLine.split("&")[5];
            ConnectedSendReceiveBox box = connected.get(seqNumber);
            if(box == null){
                box = new ConnectedSendReceiveBox();
                connected.put(seqNumber, box);
            }
            box.setSend(sendLine);
        }

        for(ConnectedSendReceiveBox box: connected.values()){
            events += box.getEvent();
        }

        writer.write(head);
        writer.write(events);
        writer.write(marks);
        writer.close();
    }

    private String workOneFile(File file, int process) throws IOException  {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        String marks = "";
        while((line = reader.readLine()) != null){
            if(line.startsWith("R")) {
                // Receiving end of the channel
                received.add(line);
            } else if(line.startsWith("S")) {
                // Send end of channel
                sent.add(line);
            } else {
                marks += saveMark("red", processes[process], line.split(" ")[1], line.split(" ")[0]);
            }
        }
        return marks;
    }

    private String saveHead(String[] processes) {
        String processArray = "";
        for(String process: processes){
            processArray += process + ",";
        }
        if(processes.length > 0){
            processArray = processArray.substring(0, processArray.length()-1);
        }
        return String.format("config:\n" +
                "  PROCESS_FONT_SIZE: 50 # pt\n" +
                "  DATA_LINE_WIDTH: 0.01 # cm\n" +
                "processes: [%s]\n", processArray);
    }

    private String saveEvent(String sourceProcess, String sourceTime, String distTime, String distProcess, String amount,
                             String color) {
        return String.format(
                "  - src: {p: %s, ts: %s}\n" +
                        "    dst: {p: %s, ts: %s}\n" +
                        "    data: %s\n" +
                        "    color: '%s'\n",
                sourceProcess.replaceAll(":","-"),
                sourceTime,
                distProcess.replaceAll(":","-"),
                distTime,
                amount.replaceAll(":","-"),
                color
        );
    }

    private String saveMark(String color, String process, String text, String timestamp) {
        return String.format("- {color: '%s', p: %s, text: '%s', ts: %s}\n",
                color,
                process.replaceAll(":","-"),
                text,
                timestamp);
    }

    private class ConnectedSendReceiveBox {
        private String received;
        private String send;

        public void setSend(String send) {
            this.send = send;
        }

        public void setReceived(String received) {
            this.received = received;
        }

        public String getEvent() {
            String[] receivedParts = received.split("&");
            String[] sendParts = send.split("&");
            String sourceProcess = sendParts[1];
            String sourceTime = sendParts[3];
            String distTime = receivedParts[3];
            String distProcess = sendParts[2];
            String amount = sendParts[4];
            String color = getColorForProcess(sourceProcess, amount);
            return saveEvent(sourceProcess,sourceTime,distTime,distProcess,amount,color);
        }

        private String getColorForProcess(String sourceProcess, String message) {
            sourceProcess = sourceProcess.replaceAll(":","-");
            if(message.startsWith("M")){
                return "#00FFFF";
            }
            else if(message.startsWith("R")){
                return "#C0C0C0";
            } else {
                return "#FF00FF";
            }
            /*for(int i = 0; i < processes.length; i++){
                if(processes[i].equals(sourceProcess)){
                    return colors[i];
                }
            }
            return "black";*/
        }
    }

    public static void main(String[] args) {
        try {
            String fileOutputPath = args[0];
            int amountOfProcesses = Integer.parseInt(args[1]);
            String[] filePaths = Arrays.copyOfRange(args, 2, 2 + amountOfProcesses);
            String[] processes = Arrays.copyOfRange(args,2+amountOfProcesses,args.length);
            new Log(filePaths, fileOutputPath, processes).write();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
