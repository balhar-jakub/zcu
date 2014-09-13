package net.balhar.zcu.ds.transaction.util;

/**
 *
 */
public class Params {
    /**
     * If the param does not exists, it throws IndexOufOfBoundsException
     *
     * @param params
     * @param index
     * @return
     */
    public static Integer getParam(String params, int index) {
        String[] splitted = params.split(" ");
        return Integer.parseInt(splitted[index]);
    }
}
