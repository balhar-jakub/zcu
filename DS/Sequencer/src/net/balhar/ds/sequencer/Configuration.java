package net.balhar.ds.sequencer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class parses configuration file and reads data from it.
 */
public class Configuration {
    private Map<String,String> configurationInfos;

    public Configuration(File configurationFile){
        configurationInfos = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configurationFile));
            String line;
            String[] valueData;
            while((line = reader.readLine()) != null){
                valueData = line.split("=");
                configurationInfos.put(valueData[0], valueData[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getConfigParam(String key){
        return configurationInfos.get(key);
    }
}
