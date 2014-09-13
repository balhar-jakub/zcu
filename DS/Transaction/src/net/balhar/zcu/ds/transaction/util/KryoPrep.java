package net.balhar.zcu.ds.transaction.util;

import com.esotericsoftware.kryo.Kryo;
import net.balhar.zcu.ds.transaction.Row;
import net.balhar.zcu.ds.transaction.comm.Abort;
import net.balhar.zcu.ds.transaction.comm.Ok;
import net.balhar.zcu.ds.transaction.operations.Add;
import net.balhar.zcu.ds.transaction.operations.Sleep;
import net.balhar.zcu.ds.transaction.operations.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class KryoPrep {
    public static void prepareKryo(Kryo kryo) {
        kryo.register(Transaction.class);
        kryo.register(Add.class);
        kryo.register(Sleep.class);
        kryo.register(Row.class);
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(Ok.class);
        kryo.register(Abort.class);
    }
}
