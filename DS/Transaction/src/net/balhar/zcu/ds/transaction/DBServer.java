package net.balhar.zcu.ds.transaction;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import net.balhar.zcu.ds.transaction.comm.Abort;
import net.balhar.zcu.ds.transaction.comm.Ok;
import net.balhar.zcu.ds.transaction.operations.Transaction;
import net.balhar.zcu.ds.transaction.util.KryoPrep;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Server has main time.
 * Whole logging is going on here. I am using logical time based on the server time. So if i send receive back to
 * client receive time of operation is expected to be send + 1;
 *
 * Logical time internal is incremented every time an operation is received.
 * Server runs asynchronously
 * Server actually receives Transaction and return transaction. It just sets transaction timestamp on it. Read
 * Timestamp is unimportant in this case.
 * It also contains the rows it needs so the server send them as a part of transaction.
 */
public class DBServer implements Runnable {
    private int actualTime = 0;
    private Log log;
    private Table table;
    private Integer port;

    public DBServer(File logFile, Integer port) throws IOException {
        this.log = new Log(logFile);
        this.port = port;

        table = new Table();
        initializeTable();
    }

    private void initializeTable(){
        Row row;
        for(int i =0; i < 100; i++){
            row = new Row();
            row.setId(i);
            row.setValue(i - 50);
            table.add(row);
        }
    }

    @Override
    public void run() {
        final Server server = new Server();
        KryoPrep.prepareKryo(server.getKryo());

        server.start();
        try {
            server.bind(port);
            server.addListener(new Listener(){
                @Override
                public void received(Connection connection, Object object) {
                    if(object instanceof Transaction) {
			            actualTime++;
                        Transaction transaction = (Transaction) object;
                        System.out.println("Transaction Received " + transaction.getFrom());
                        if(transaction.getTimestamp() == null){
                            log.log(actualTime-1, actualTime, transaction.getFrom(), "server", "BEGIN");
                            transaction.setTimestamp(actualTime);
                            List<Integer> rows = transaction.getNeededRows();
                            for(Integer row: rows) {
                                transaction.addNeeded(table.get(row));
                            }
                            log.log(actualTime, actualTime+1, "server", transaction.getFrom(), transaction.getString());
                            actualTime++;
                            connection.sendTCP(transaction);
                        } else {
                            log.log(actualTime-1, actualTime, transaction.getFrom(), "server", "COMMIT");
                            // Handle transaction and either return OK or ABORT
                            if(handleTransaction(transaction)) {
                                log.log(actualTime, actualTime+1, "server", transaction.getFrom(), "OK");
                                actualTime++;
                                connection.sendTCP(new Ok());
                            } else {
                                log.log(actualTime, actualTime+1, "server", transaction.getFrom(), "ABORT");
                                actualTime++;
                                connection.sendTCP(new Abort());
                            }
                        }
                    } else {
			            System.out.println("Stop server");
                        server.stop();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't bind port. ");
        }
    }

    private boolean handleTransaction(Transaction transaction) {
        Collection<Row> updatedRows = transaction.getRows();
        Row toCompare;
        for(Row row: updatedRows) {
            toCompare = table.get(row.getId());
            if(toCompare.getWts() > row.getWts()) {
                return false;
            }
            toCompare.setValue(row.getValue());
            toCompare.setWts(row.getWts());
        }

        return true;
    }

    public static void main(String[] args) {
        Integer port = Integer.parseInt(args[0]);
        File log = new File(args[1]);
        try {
            new Thread(new DBServer(log, port)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
