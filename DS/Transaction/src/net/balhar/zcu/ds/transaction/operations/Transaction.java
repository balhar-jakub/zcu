package net.balhar.zcu.ds.transaction.operations;

import net.balhar.zcu.ds.transaction.Row;
import net.balhar.zcu.ds.transaction.operations.Add;
import net.balhar.zcu.ds.transaction.operations.IOperation;

import java.util.*;

/**
 * This class simply contains information about what operations are in the transaction, it also can find out,
 * what is happening on the borderlines of the transactions.
 *
 */
public class Transaction implements IOperation {
    private Integer timestamp = null;
    private HashMap<Integer, Row> neededRows = new HashMap<Integer, Row>();
    private String from;
    private List<IOperation> operations;

    public Transaction(){
        operations = new ArrayList<IOperation>();
    }

    public List<IOperation> getOperations() {
        return operations;
    }

    public void add(IOperation operation){
        operations.add(operation);
    }

    public List<Integer> getNeededRows() {
        Set<Integer> neededRows = new HashSet<Integer>();
        for(IOperation operation: getOperations()) {
            if(operation instanceof Add) {
                Add add = (Add) operation;
                neededRows.add(add.getSrcRow1());
                neededRows.add(add.getSrcRow2());
                neededRows.add(add.getDstRow());
            }
        }
        return new ArrayList<Integer>(neededRows);
    }

    public void addNeeded(Row row) {
        neededRows.put(row.getId(), row);
    }

    public Row getNeededRow(Integer id) {
        return neededRows.get(id);
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public Collection<Row> getRows() {
        return neededRows.values();
    }

    public String getString(){
        String repr = "";
        for(IOperation operation: operations) {
            repr += operation.getString() + " -> ";
        }
        return repr;
    }
}
