package net.balhar.zcu.ds.transaction.operations;

/**
 *
 */
public class Add implements IOperation {
    private Integer srcRow1;
    private Integer srcRow2;
    private Integer dstRow;

    public Integer getSrcRow1() {
        return srcRow1;
    }

    public void setSrcRow1(Integer srcRow1) {
        this.srcRow1 = srcRow1;
    }

    public Integer getSrcRow2() {
        return srcRow2;
    }

    public void setSrcRow2(Integer srcRow2) {
        this.srcRow2 = srcRow2;
    }

    public Integer getDstRow() {
        return dstRow;
    }

    public void setDstRow(Integer dstRow) {
        this.dstRow = dstRow;
    }

    @Override
    public String getString() {
        return String.format("%s %s %s %s",
                "ADD",
                String.valueOf(srcRow1),
                String.valueOf(srcRow2),
                String.valueOf(dstRow)
        );
    }
}
