package net.balhar.zcu.ds.transaction.log;

import java.io.*;
import java.util.*;

/**
 *
 */
public class Log {
    private File file;
    private File output;
    private String[] processes;
    private String[] colors;

    public Log(String filePath, String fileOutputPath, String... processes) {
        file = new File(filePath);
        output = new File(fileOutputPath);
        this.processes = processes;
        for(int i = 0; i < processes.length; i++){
            processes[i] = processes[i].replaceAll(":","-");
        }
        colors = new String[]{"#FF0000","#0000FF","#00FF00","#000000","#FF00FF"};
    }

    public void write() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        String head = saveHead(processes);
        String marks = "";
        String events = "traffic:\n";

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while((line = reader.readLine()) != null){
            String[] lineParts = line.split(";");
            String color = getColor(lineParts[0]);
            events += saveEvent(lineParts[0], lineParts[1], lineParts[2], lineParts[3], lineParts[4], color);
        }

        writer.write(head);
        writer.write(events);
        writer.write(marks);
        writer.close();
    }

    private String getColor(String sourceProcess){
        for(int i = 0; i < processes.length; i++){
            if(processes[i].equals(sourceProcess)){
                return colors[i];
            }
        }
        return "black";
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

    private String saveEvent(String sourceProcess, String sourceTime, String distProcess, String distTime, String amount,
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

    public static void main(String[] args) {
        try {
            String fileOutputPath = args[0];
            String filePath = args[1];
            String[] processes = Arrays.copyOfRange(args,2,args.length);
            new Log(filePath, fileOutputPath, processes).write();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
