package ru.redandspring.robotrum;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Alexander Tretyakov.
 */
class Log {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");

    private static Map<String, Long> pt = new HashMap<>();

    static void info(String str){
        String timeStamp = formatter.format(Calendar.getInstance().getTime());
        System.out.println(timeStamp + " " + str);
    }

    static void point(String label){
        final long currentTime = System.currentTimeMillis();

        if (pt.containsKey(label)){
            long time = currentTime - pt.get(label);
            if (time > 10000) {
                String result = String.format("%02d:%02d:%02d", time / 1000 / 3600, time / 1000 / 60 % 60, time / 1000 % 60);
                Log.info("Time point: [ " + label + " ] " + result);
            }
            pt.remove(label);
        }
        else {
            pt.put(label, currentTime);
        }
    }
}
