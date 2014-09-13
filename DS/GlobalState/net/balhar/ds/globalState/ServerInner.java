package net.balhar.ds.globalState;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import net.balhar.ds.globalState.transfer.Marker;
import net.balhar.ds.globalState.transfer.Operation;
import net.balhar.ds.globalState.transfer.Return;
import net.balhar.ds.globalState.transfer.Time;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * It is used for sending data. Internally it is a Thread which works on its own.
 * Other parts only register that they want to send something.
 * When the time comes it takes its inner queue and sends it one by one.
 */
public class ServerInner implements Runnable {
    private final List<IDestination> queue;
    private Log log;

    public ServerInner(Log log) {
        this.log = log;
        this.queue = new ArrayList<IDestination>();
    }

    @Override
    public void run() {
        for(;;){
            try {
                Thread.sleep(Time.TIME_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                handleQueue();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void handleQueue() throws IOException {
        synchronized (queue) {
            for(IDestination toSend: queue) {
                sendObject(toSend);
            }
            queue.retainAll(new ArrayList<IDestination>());
        }
    }

    private void sendObject(IDestination toSend) throws IOException {
        Client client = new Client();
        ServerInner.prepareKryo(client.getKryo());
        new Thread(client).start();
        client.connect((100 * Time.TIME_UNIT), toSend.getDestination().getAddress(), toSend.getDestination().getPort());
        log.logSend(toSend);
        client.sendTCP(toSend);
    }


    public void sendTime(Time time) {
        addToQueue(time);
    }

    public void sendMarker(Marker marker) {
        addToQueue(marker);
    }

    public void sendOperation(Operation operation) {
        addToQueue(operation);
    }

    public void sendReturn(Return aReturn) {
        addToQueue(aReturn);
    }

    private void addToQueue(IDestination object) {
        synchronized (queue) {
            queue.add(object);
        }
    }

    public static void prepareKryo(Kryo kryo){
        kryo.register(Marker.class);
        kryo.register(Operation.class);
        kryo.register(Return.class);
        kryo.register(Time.class);
        kryo.register(ClientInfo.class);
        kryo.register(UUID.class);
    }
}
