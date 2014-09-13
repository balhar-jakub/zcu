package net.balhar.zcu.ds.transaction;

import net.balhar.zcu.ds.transaction.comm.ServerBundle;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Main {
    public static void main(String[] args) {
        try {
            DBServer server = new DBServer(new File("logFile"), 5468);
            new Thread(server).start();

            DBClient client = new DBClient(new ServerBundle("localhost", 5468), new File("transactions"), "bank1");
            new Thread(client).start();
            DBClient client1 = new DBClient(new ServerBundle("localhost", 5468), new File("transactions"), "bank2");
            new Thread(client1).start();
            DBClient client2 = new DBClient(new ServerBundle("localhost", 5468), new File("transactions"), "bank3");
            new Thread(client2).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
