package util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by firefix on 2015/9/23.
 */
public class Log {

    private static Properties p = ConfigHandler.getProperties();

    private static int logType = Integer.valueOf(p.getProperty("logType"));
    private static int logLevel = Integer.valueOf(p.getProperty("logLevel"));
    private static String logFilename = p.getProperty("logFilename");
    private static String getTimeString() {
        Date now = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
        return time.format(now);
    }
    private static void log0(String tag, String fileName, String loglevel, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(getTimeString() + "[" + loglevel + "] || " + tag + " : " + content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static void log1(String tag, String loglevel, String content) {
        System.out.println(getTimeString() + "[" + loglevel + "] || " + tag + " : " + content);
    }
    public static void v(String tag, String content) {
        if(logLevel <= 1){
            if(logType == 0) {
                log0(tag, logFilename, "WARN", content);
            } else if (logType == 1) {
                log1(tag, "WARN", content);
            }
        } else {
            return;
        }
    }
    public static void i(String tag, String content) {
        if(logLevel == 0) {
            if (logType == 0) {
                log0(tag, logFilename, "INFO", content);
            } else if (logType == 1) {
                log1(tag, "INFO", content);
            }
        }
    }

    public static void e(String tag, String content) {
        if(logType == 0) {
            log0(tag, logFilename, "ERROR", content);
        } else if (logType == 1) {
            log1(tag, "ERROR", content);
        }

    }

    public static void main(String args[]) {
        Log.i(Log.class.getName(), " test");

        Log.v(Log.class.getName(), " test");
        Log.e(Log.class.getName(), " test");
    }
}
