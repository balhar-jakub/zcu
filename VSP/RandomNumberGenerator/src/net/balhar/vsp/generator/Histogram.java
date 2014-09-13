package net.balhar.vsp.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Histogram {
    private Map<Long,Integer> histogramDistribution;

    public Histogram(){
        histogramDistribution = new HashMap<Long, Integer>();
    }

    public void addNew(long nextPoisson) {
        int amountOfValues = 1;
        if(histogramDistribution.containsKey(nextPoisson)) {
            amountOfValues = histogramDistribution.get(nextPoisson);
            amountOfValues++;
        }
        histogramDistribution.put(nextPoisson,amountOfValues);
    }

    private String getNChars(int n){
        String result = "";
        for(int i = 0; i < n; i++) {
            result += "*";
        }
        return result;
    }

    public String show() {
        Set<Long> keys = histogramDistribution.keySet();
        String result = "";
        for(Long key: keys){
            result += String.format("%s: %s\n", String.valueOf(key), String.valueOf(getNChars(histogramDistribution.get(key))));
        }
        return result;
    }
}
