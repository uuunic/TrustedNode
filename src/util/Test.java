package util;

import java.awt.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by firefix on 2015/9/28.
 */
public class Test {
    public static void main(String[] args) {
        Properties p = ConfigHandler.getProperties();
        String[] names = p.getProperty("name").trim().split(",");
        String[] IPs = p.getProperty("IP").trim().split(",");
        for(String IP : IPs){
            System.out.println(IP);
        }
        for(String name : names) {
            System.out.println(name);
        }

    }
}
