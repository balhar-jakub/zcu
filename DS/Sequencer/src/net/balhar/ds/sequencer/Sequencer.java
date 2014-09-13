package net.balhar.ds.sequencer;

import net.balhar.ds.sequencer.comm.Client;

import java.io.IOException;
import java.net.*;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * It is easier than the bank because it is itself only server. It does not serve as client and server.
 * I think that it should be possible for Bank to serve only as a client. The trouble is, can I have
 * opened more than one UDP connection.
 * <p/>
 * Sequencer must definitively accept incoming connections and send data to all connected banks.
 * So it must manage list of banks in other it received their connections.
 */
public class Sequencer implements Runnable {
    // List of operations, which should happen.
    private Collection<Client> clients = new Vector<Client>();

    private int sequenceNumber = 0;

    private int clientsConnected = 0;
    private Logging log;

    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;
    private DatagramPacket datagramPacket;
    private ExecutorService executorService;

    public Sequencer(int port, int logPort) {
        log = new Logging(logPort, 0);
        executorService = Executors.newFixedThreadPool(10);
        try {
            clientSocket = new DatagramSocket();
            serverSocket = new DatagramSocket(port);
            serverSocket.setSoTimeout(Request.TIMEOUT);
            datagramPacket = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("It was not possible to open the socket.");
        }
    }

    @Override
    public void run() {
        new Thread(new Server()).start();
    }

    private void sendToAllClients(Operation operation){
        for (Client client : clients) {
            operation.setOriginationTime(System.currentTimeMillis());
            byte[] sendData = operation.serialize().getBytes();
            System.out.println("Send From Sequencer: " + new String(sendData) + " To: " + client.getPort());
            sendData(client,sendData);
        }
    }

    private void sendData(Client client, byte[] sendData){
        try {
            InetAddress IPAddress = InetAddress.getByName(client.getAddressName());

            datagramPacket.setAddress(IPAddress);
            datagramPacket.setPort(client.getPort());

            datagramPacket.setData(sendData);
            datagramPacket.setLength(sendData.length);

            clientSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void finish() {
        serverSocket.close();
        clientSocket.close();
        executorService.shutdownNow();
        System.out.println("Sequencer Timeout");
    }

    private class Server implements Runnable{
        @Override
        public void run() {
            final DatagramPacket packet = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            for(;;){
                try {
                    serverSocket.receive(packet);
                    executorService.execute(new Job(new String(packet.getData()), packet.getAddress().getHostAddress()));
                } catch (SocketTimeoutException ex) {
                    finish();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Job implements Runnable {
        private String address;
        private String data;

        Job(String data, String address){
            this.address = address;
            this.data = data;
        }

        @Override
        public void run() {
            System.out.println("Sequencer Received: " + data);
            Operation operation = Operation.deserialize(data);

            if(clientsConnected < 3){
                Client client = new Client(address, operation.getPort());
                clients.add(client);
                clientsConnected++;
                if(clientsConnected >= 3){
                    for(Client clientToSend: clients){
                        sendData(clientToSend, "READY".getBytes());
                    }
                }
            } else {
                synchronized(this){
                    operation.setSequenceNumber(++sequenceNumber);
                }

                Event event = new Event();
                event.setDistTime(String.valueOf(System.currentTimeMillis()));
                event.setDistProcess("sequencer");
                event.setSourceProcess(operation.getIdentifier());
                event.setSourceTime(String.valueOf(operation.getOriginationTime()));
                event.setData(operation);

                log.logEvent(event);

                sendToAllClients(operation);
            }
        }
    }

    public static void main(String[] args) {
        Sequencer sequencer;
        if (args.length == 0) {
            sequencer = new Sequencer(5555,4982);
        } else {
            sequencer = new Sequencer(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
        }
        new Thread(sequencer).start();
    }
}
