package net.balhar.ds.globalState;

import net.balhar.ds.globalState.transfer.Marker;
import net.balhar.ds.globalState.transfer.Operation;
import net.balhar.ds.globalState.transfer.Return;
import net.balhar.ds.globalState.transfer.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class Branch implements Runnable {
    private final Object timeSemaphore = new Object();
    private final List<Object> queue;
    private List<ClientInfo> clients;

    private Integer actualState = 1000;
    private Integer actualTime = 0;

    private Integer stateOfSnapshot = null;
    private List<ClientInfo> redClients;
    private List<ClientInfo> returned;
    private boolean startSnapshotToken;
    private ClientInfo initiator;

    ClientInfo me;

    private ServerInner server;
    private ClientInner client;
    private Log log;


    public Branch(boolean startSnapshotToken, ClientInfo me, String logFile, List<ClientInfo> clients) {
		System.out.println("Me " + me.serialize() + " " + startSnapshotToken);
        queue = new ArrayList<Object>();
        this.clients = clients;
        this.startSnapshotToken = startSnapshotToken;

        log = new Log(this, logFile);
        server = new ServerInner(log);
        client = new ClientInner(log, me.getPort(), this);

        this.me = me;
    }

    private void incrementHours(){
        synchronized(timeSemaphore){
            actualTime++;
        }
    }

    private void setHours(Integer hours) {
        synchronized(timeSemaphore){
            if(hours > actualTime){
                actualTime = hours + 1;
            }
        }
    }

    @Override
    public void run() {
        // Start Logging Thread
        new Thread(log).start();
        // Start ClientInner Thread
        new Thread(client).start();
        // Start ServerInner Thread
        new Thread(server).start();

        try {
            Thread.sleep(2000 * Time.TIME_UNIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(startSnapshotToken) {
            actualTime = 0;
            sendTime(actualTime);
        }

        int MAX_OPERATIONS = 10;
        int actualOperation = 0;
        for(;;) {
            try {
                Thread.sleep(Time.TIME_UNIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            handleQueue();
            if(actualTime == null){
                continue;
            }

            actualOperation++;
            if(actualOperation <= MAX_OPERATIONS){
                if(Math.random() < 0.25){
                    if(startSnapshotToken) {
                        if(stateOfSnapshot == null){
                            startSnapshot(me);
                        }
                    }
                }
                sendOperation();
            }
        }
    }

    public Integer getTime() {
        synchronized(timeSemaphore) {
            return actualTime;
        }
    }

    private void sendTime(Integer actualTime) {
        for(ClientInfo clientInfo: clients) {
            server.sendTime(new Time(actualTime, clientInfo));
        }
    }

    private void startSnapshot(ClientInfo initiator) {
        this.initiator = initiator;
        stateOfSnapshot = actualState;
        redClients = new ArrayList<ClientInfo>();
        returned = new ArrayList<ClientInfo>();
        sendMarkers(initiator);
    }

    private void sendMarkers(ClientInfo initiator) {
        for(ClientInfo clientInfo: clients) {
            incrementHours();
            server.sendMarker(new Marker(me, UUID.randomUUID().toString(), clientInfo, actualTime, initiator));
        }
    }

    private void handleQueue() {
        synchronized(queue){
            if(queue.isEmpty()) {
                return;
            }

            for(Object object: queue) {
                if(object instanceof Operation){
                    setHours(((IDestination)object).getTime());
                    handleOperation((Operation) object);
                } else if(object instanceof Marker) {
                    setHours(((IDestination)object).getTime());
                    handleMarker((Marker)object);
                } else if(object instanceof Return) {
                    setHours(((IDestination)object).getTime());
                   handleReturn((Return) object);
                } else {
                    handleTime((Time) object);
                }
            }

            queue.retainAll(new ArrayList<Object>());
        }
    }

    private void handleTime(Time object) {
        actualTime = object.getTime();
    }

    public void addToQueue(Object toAdd) {
        synchronized(queue) {
            queue.add(toAdd);
        }
    }

    private void handleReturn(Return returnObj) {
        returned.add(returnObj.getSource());
        stateOfSnapshot += returnObj.getState();

        if(returned.containsAll(clients)){
            log.logGlobal(stateOfSnapshot);

            cleanSnapshot();
        }
    }

    private void cleanSnapshot() {
        redClients = null;
        returned = null;
        stateOfSnapshot = null;
        initiator = null;
    }

    private void handleMarker(Marker marker) {
        if(stateOfSnapshot == null) {
			System.out.println("Started snapshot on " + me.serialize());
            startSnapshot(marker.getInitiator());

            initiator = marker.getInitiator();
            redClients.add(marker.getSource());
        } else {
			System.out.println("Received marker from " + marker.getSource().serialize() + " to " + me.serialize());
            redClients.add(marker.getSource());

            if(redClients.containsAll(clients) && !initiator.equals(me)){
                incrementHours();
                server.sendReturn(new Return(stateOfSnapshot, UUID.randomUUID().toString(), initiator, me, actualTime));

                cleanSnapshot();
            }
        }
    }

    private void handleOperation(Operation operation) {
        actualState += operation.getSentAmount();

        if(stateOfSnapshot == null){
            if(operation.getSnapshotStarted()) {
                //System.out.println("Received amount from Red Branch and therefore the algorithm does not work.");
            }
        }  else {
            if(redClients.contains(operation.getSource())) {
                // Received from red and therefore is already contained in the global state.
            } else {
                // Received from white and therefore is not contained in the global state. Add it to the global state.
                stateOfSnapshot += operation.getSentAmount();
            }
        }
    }

    private void sendOperation() {
        int client = (int) (Math.random() * clients.size());
        int amount = ((Long)Math.round((Math.random() * 100))).intValue();
        actualState -= amount;
        incrementHours();
        server.sendOperation(new Operation(amount, me, stateOfSnapshot != null, UUID.randomUUID().toString(), clients.get(client), actualTime));
    }

    public static void main(String[] args){
        ClientInfo me = new ClientInfo(Integer.parseInt(args[0].split(":")[1]), args[0].split(":")[0]);
        String logFile = args[1];
        Boolean snapshotAllowed = Boolean.parseBoolean(args[2]);
        String[] clients = Arrays.copyOfRange(args, 3, args.length);
        List<ClientInfo> clientsFinal = new ArrayList<ClientInfo>();
        String[] clientParts;
        for(String client: clients) {
            clientParts = client.split(":");
            clientsFinal.add(new ClientInfo(Integer.parseInt(clientParts[1]), clientParts[0]));
        }
        Branch branch = new Branch(snapshotAllowed,me,logFile,clientsFinal);
        new Thread(branch).start();
    }
}
