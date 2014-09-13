package net.balhar.zcu.ds.transaction;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import net.balhar.zcu.ds.transaction.comm.Abort;
import net.balhar.zcu.ds.transaction.comm.Ok;
import net.balhar.zcu.ds.transaction.comm.ServerBundle;
import net.balhar.zcu.ds.transaction.operations.Add;
import net.balhar.zcu.ds.transaction.operations.IOperation;
import net.balhar.zcu.ds.transaction.operations.Sleep;
import net.balhar.zcu.ds.transaction.operations.Transaction;
import net.balhar.zcu.ds.transaction.util.KryoPrep;
import net.balhar.zcu.ds.transaction.util.Params;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * It gets address of the server.
 * It reads operation, it needs to process, from the file given as argument.
 * It simply receives data it asked for from server.
 * It does the operation on them.
 * If the operation succeed, it is done
 * Otherwise it is aborted and therefore I return to the beginning of the transaction.
 * DbClient parses the file it receives into Transactions and Sleeps.
 */
public class DBClient implements Runnable {
    private int MAX_WAITING_TIME_MS = 100;

    private ServerBundle server;
    private List<IOperation> transactions = new ArrayList<IOperation>();
    private Boolean waitingForServerResponse;
    private Transaction actualTransaction;
    private Boolean aborted;
    private String from;

    public DBClient(ServerBundle server, File transactions, String from) throws IOException {
        this.server = server;
        this.from = from;

        BufferedReader reader = new BufferedReader(new FileReader(transactions));
        String line;
        Transaction actualTransaction = new Transaction();
        while((line = reader.readLine()) != null) {
            if(line.startsWith("ADD")) {
                Add add = new Add();
                add.setSrcRow1(Params.getParam(line, 1));
                add.setSrcRow2(Params.getParam(line, 2));
                add.setDstRow(Params.getParam(line, 3));

                assert actualTransaction != null;
                actualTransaction.add(add);
            } else if(line.startsWith("COMMIT")) {
                this.transactions.add(actualTransaction);
                actualTransaction = null;
            } else if(line.startsWith("BEGIN")) {
                actualTransaction = new Transaction();
                actualTransaction.setFrom(from);
            } else if(line.startsWith("SLEEP")) {
                Sleep sleep = new Sleep();
                sleep.setSleepTime(Params.getParam(line, 1));
                if(actualTransaction != null) {
                    actualTransaction.add(sleep);
                } else {
                    this.transactions.add(sleep);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IOperation operation;
        for(int i=0; i < transactions.size(); i++) {
	        System.out.println("Send operation " + i + " From: " + from);
            operation = transactions.get(i);

            if(operation instanceof Sleep) {
                try {
                    System.out.println("Sleeping " + ((Sleep) operation).getSleepTime());
                    Thread.sleep(((Sleep) operation).getSleepTime());
                } catch (InterruptedException e) {
                    // Do nothing
                }
            } else if(operation instanceof Transaction) {
                try {
                    sendTransaction((Transaction) operation);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AbortedException ex) {
                    // Revert the transaction so it is called again.
                    i--;
                }
            } else {
                throw new RuntimeException("Some weird operation got into the transactions stack.");
            }
        }
    }

    private void sendTransaction(Transaction transaction) throws IOException, AbortedException {
        System.out.println("Send Transaction " + from);
        final Client client = new Client();
        KryoPrep.prepareKryo(client.getKryo());
        actualTransaction = null;
        client.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object o) {
                System.out.println("Received");
                if(o instanceof Transaction) {
                    // Now I have transaction
                    actualTransaction = (Transaction) o;
                } else {
                    System.out.println("Wrong data");
                }
                waitingForServerResponse = false;
            }
        });
        new Thread(client).start();
        client.connect(MAX_WAITING_TIME_MS, server.getAddress(), server.getPort());
        client.sendTCP(transaction);

        waitingForServerResponse = true;
        // Another thread sets waitingForServerResponse;
        waitForResponse();
        // I recevied response and so this Thread can continue
        handleTransactionData(actualTransaction);
    }

    private void waitForResponse() {
        System.out.println("Wait for response " + from);
        while(waitingForServerResponse) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTransactionData(Transaction actualTransaction) throws IOException, AbortedException {
        for(IOperation operation: actualTransaction.getOperations()) {
            if(operation instanceof Add) {
                Add add = (Add) operation;
                Row srcRow1 = actualTransaction.getNeededRow(add.getSrcRow1());
                Row srcRow2 = actualTransaction.getNeededRow(add.getSrcRow2());
                Row dstRow = actualTransaction.getNeededRow(add.getDstRow());
                dstRow.setValue(srcRow1.getValue() + srcRow2.getValue());
                dstRow.setWts(actualTransaction.getTimestamp());
            } else if(operation instanceof Sleep) {
                try {
                    Thread.sleep(((Sleep) operation).getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException("Wrong type of operation got into the transaction.");
            }
        }

        sendTransactionResponseData();
    }

    private void sendTransactionResponseData() throws IOException, AbortedException {
        final Client client = new Client();
        KryoPrep.prepareKryo(client.getKryo());
        aborted = false;
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                if (o instanceof Abort) {
                    aborted = true;
                } else if (o instanceof Ok) {
                    // Do Nothing correct
                } else {
                    client.close();
                }
                waitingForServerResponse = false;
            }
        });
        new Thread(client).start();
        client.connect(MAX_WAITING_TIME_MS, server.getAddress(), server.getPort());
        client.sendTCP(actualTransaction);
        actualTransaction = null;

        waitingForServerResponse = true;
        waitForResponse();
        if(aborted) {
            throw new AbortedException();
        }
    }

    public static void main(String[] args) {
        ServerBundle server = new ServerBundle(args[0], Integer.parseInt(args[1]));
        File transactions = new File(args[2]);
        String name = args[3];
        try {
            DBClient client = new DBClient(server,transactions,name);
            new Thread(client).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
