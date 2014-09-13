package net.balhar.ds.sequencer;

import java.io.IOException;
import java.net.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 5.11.13
 * Time: 17:57
 */
public class Logging {
    private int eventPort;
    private int markPort;

    private DatagramSocket markSocket;
    private DatagramSocket eventSocket;

    private DatagramPacket markPacket;
    private DatagramPacket eventPacket;

    public Logging(int eventPort, int markPort){
        this.eventPort = eventPort;
        this.markPort = markPort;

        try {
            markSocket = new DatagramSocket();
            eventSocket = new DatagramSocket();

            markPacket = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            markPacket.setAddress(InetAddress.getByName("localhost"));

            eventPacket = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            eventPacket.setAddress(InetAddress.getByName("localhost"));
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("Iretrievable error when starting log.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Iretrievable error when starting log.");
        }
    }

    public void logMark(Mark mark) {
        markPacket.setPort(markPort);
        System.out.println("Log Mark: " + mark.serialize());
        send(mark.serialize(), markSocket, markPacket);
    }

    public void logEvent(Event event) {
        eventPacket.setPort(eventPort);
        System.out.println("Log Event: " + event.serialize());
        send(event.serialize(), eventSocket, eventPacket);
    }

    private void send(String data, DatagramSocket socket, DatagramPacket packet){
        try {
            byte[] dataToSend = data.getBytes();
            packet.setData(dataToSend);
            packet.setLength(dataToSend.length);

            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }
}
