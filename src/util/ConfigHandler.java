package util;

import java.io.*;
import java.util.Properties;

/**
 * Created by firefix on 2015/9/23.
 */
public class ConfigHandler {
    private static Properties p  = null;
    private ConfigHandler(){
    }
    public static Properties getProperties(){
        if (p == null) {
            ConfigHandler ch = new ConfigHandler();
            p = new Properties();
            InputStream instream = null;
            try {
                instream = new BufferedInputStream(new FileInputStream("config.properties"));
             p.load(instream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return p;
    }

}
