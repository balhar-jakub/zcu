package net.balhar.zcu.ds.transaction;

import java.util.ArrayList;

/**
 *
 */
public class Row {
    private int wts = 0;
    private int value = 0;
    private int id = 0;

    public int getWts() {
        return wts;
    }

    public void setWts(int wts) {
        this.wts = wts;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (id != row.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
