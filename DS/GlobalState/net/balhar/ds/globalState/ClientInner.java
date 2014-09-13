package net.balhar.ds.globalState;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 *
 */
public class ClientInner implements Runnable {
    private Log log;
    private Branch branch;
    private int port;


    public ClientInner(Log log, int port, Branch branch) {
        this.log = log;
        this.port = port;
        this.branch = branch;
    }

    @Override
    public void run() {
        Server server = new Server();
        ServerInner.prepareKryo(server.getKryo());

        System.out.println("Server on port " + port);
        server.start();
        try {
            server.bind(port);
            server.addListener(new Listener(){
                @Override
                public void received(Connection connection, Object object) {
                    if(object instanceof IDestination){
                        log.logReceived((IDestination)object);
                        branch.addToQueue(object);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't bind port. ");
        }
    }
}
