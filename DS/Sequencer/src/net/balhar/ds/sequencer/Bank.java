package net.balhar.ds.sequencer;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents one bank. Bank has actually just one account. So Bank either generates or load
 * operations which change balance on the Bank account.
 * When The operation is initiated, it is sent to the Sequencer. Sequencer send it to all banks in given
 * order.
 */
public class Bank implements Runnable {
    private int sequenceNumber = 0;
    private Double accountState;

    private List<Operation> queueOfOperation = new ArrayList<Operation>();

    private Configuration config;
    private String identifier;
    private int portOfThisBank;

    private DatagramSocket serverSocket;
    private DatagramSocket clientSocket;
    private DatagramPacket sendPacket;

    private ExecutorService executorService;
    private boolean ready = false;
    private Logging log;

    public Bank(String addressName, String configurationPath) {
        accountState = 100.0d;
        config = new Configuration(new File(configurationPath));
        identifier = config.getConfigParam("name");
        executorService = Executors.newFixedThreadPool(10);
        portOfThisBank = Integer.parseInt(config.getConfigParam("port"));
        log = new Logging(
                Integer.parseInt(config.getConfigParam("log")),
                Integer.parseInt(config.getConfigParam("mark"))
        );

        try {
            serverSocket = new DatagramSocket(portOfThisBank);
            serverSocket.setSoTimeout(Request.TIMEOUT);
            InetAddress IPAddress = InetAddress.getByName(addressName);

            clientSocket = new DatagramSocket();
            sendPacket = new DatagramPacket(new byte[Request.BYTES], Request.BYTES, IPAddress,
                    Integer.parseInt(config.getConfigParam("sequencer")));
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException("Socket Exception");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Unknown host");
        }
    }

    @Override
    public void run() {
        new Thread(new Server()).start();

        Operation op = Operation.deserialize("START;10;null;0000;" + portOfThisBank + ";" + identifier + ";");
        op.setPort(portOfThisBank);
        sendOne(op);
        waitForSequencer();
        sendOperations();
    }

    private void finish(){
        System.out.println(identifier + " Timeout");
        serverSocket.close();
        clientSocket.close();
        executorService.shutdown();
    }

    private void sendOperations() {
        int amountOfOperations = Integer.parseInt(config.getConfigParam("amountOfOperation"));
        for (int i = 1; i < amountOfOperations; i++) {
            Operation op =
                    Operation.deserialize(
                            config.getConfigParam(
                                    String.format("operation%s", String.valueOf(i))));
            op.setPort(portOfThisBank);
            sendOne(op);
        }
    }

    private void waitForSequencer() {
        for(;;){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(ready){
                break;
            }
        }
    }

    private synchronized void handleQueue(Operation op) {
        queueOfOperation.add(op);

        // This could be more efficient. Instead of just Sorting the queue you can be careful about adding information to Queue
        Collections.sort(queueOfOperation, new Comparator<Operation>() {
            @Override
            public int compare(Operation o1, Operation o2) {
                if (o1.getSequenceNumber().equals(o2.getSequenceNumber())) {
                    return 0;
                }
                return o1.getSequenceNumber() < o2.getSequenceNumber() ? -1 : 1;
            }
        });

        List<Operation> handledOperations = new ArrayList<Operation>();
        for (Operation operation : queueOfOperation) {
            if (operation.getSequenceNumber() != null &&
                    operation.getSequenceNumber() == (sequenceNumber + 1)) {
                sequenceNumber = operation.getSequenceNumber();
                handleOperation(operation);
                handledOperations.add(operation);
                Mark mark = new Mark(
                        identifier,
                        String.valueOf(operation.getType()) + ":" + accountState,
                        String.valueOf(System.currentTimeMillis())
                );
                log.logMark(mark);
            }
        }
        queueOfOperation.removeAll(handledOperations);
    }

    private void handleOperation(Operation operation) {
        switch (operation.getType()) {
            case WITHDRAWAL:
                accountState = accountState - operation.getAmount();
                break;
            case DEPOSIT:
                accountState = accountState + operation.getAmount();
                break;
            case INTEREST:
                accountState = accountState + (accountState * (operation.getAmount() / 100));
                break;
        }
    }



    private void sendOne(Operation operation) {
        try {
            operation.setOriginationTime(System.currentTimeMillis());
            byte[] sendData = operation.serialize().getBytes();
            System.out.println(identifier + " Send " + new String(sendData));

            sendPacket.setData(sendData);
            sendPacket.setLength(sendData.length);
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Server implements Runnable {
        @Override
        public void run() {
            final DatagramPacket packet = new DatagramPacket(new byte[Request.BYTES], Request.BYTES);
            for(;;){
                try {
                    serverSocket.receive(packet);
                    executorService.execute(new Job(new String(packet.getData())));
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
        String data;

        Job(String data){
            this.data = data;
        }

        @Override
        public void run() {
            if(data.startsWith("READY")){
                ready = true;
                return;
            }
            System.out.println(identifier + " Received " + data);
            Operation operation = Operation.deserialize(data);

            Event event = new Event();
            event.setDistTime(String.valueOf(System.currentTimeMillis()));
            event.setDistProcess(identifier);
            event.setSourceProcess("sequencer");
            event.setSourceTime(String.valueOf(operation.getOriginationTime()));
            event.setData(operation);

            log.logEvent(event);

            handleQueue(operation);
        }
    }

    public static void main(String[] args) {
        Bank bank;
        if (args.length == 0) {
            bank = new Bank("localhost", "config/bank1");
        } else if (args.length == 1) {
            bank = new Bank("localhost", args[0]);
        } else {
            bank = new Bank(args[1], args[0]);
        }
        new Thread(bank).start();
    }
}
