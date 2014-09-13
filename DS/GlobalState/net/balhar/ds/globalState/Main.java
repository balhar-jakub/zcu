package net.balhar.ds.globalState;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Main {
    public static void main(String[] args){
        ClientInfo clientInfo1 = new ClientInfo(5001,"127.0.0.1");
        ClientInfo clientInfo2 = new ClientInfo(5002,"127.0.0.1");
        ClientInfo clientInfo3 = new ClientInfo(5003,"127.0.0.1");

        List<ClientInfo> clients1 = new ArrayList<ClientInfo>();
        clients1.add(clientInfo2);
        clients1.add(clientInfo3);

        List<ClientInfo> clients2 = new ArrayList<ClientInfo>();
        clients2.add(clientInfo1);
        clients2.add(clientInfo3);

        List<ClientInfo> clients3 = new ArrayList<ClientInfo>();
        clients3.add(clientInfo1);
        clients3.add(clientInfo2);

        Branch branch1 = new Branch(true, clientInfo1,"logBranch1", clients1);
        Branch branch2 = new Branch(false, clientInfo2,"logBranch2", clients2);
        Branch branch3 = new Branch(false, clientInfo3,"logBranch3", clients3);

        new Thread(branch1).start();
        new Thread(branch2).start();
        new Thread(branch3).start();
    }
}
