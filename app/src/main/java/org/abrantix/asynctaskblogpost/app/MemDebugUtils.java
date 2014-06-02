package org.abrantix.asynctaskblogpost.app;

import android.os.Debug;
import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by fabrantes on 02/06/14.
 */
public class MemDebugUtils {

    public static byte[] allocMegs(int nmegs) {
        return new byte[1024 * 1024 * nmegs];
    }

    public static void logMemStatus(String tag) {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        Log.d(tag, "debug. =================================");
        Log.d(tag, "debug.heap native: allocated " + df.format(allocated) + "MB of " +  df
                .format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d(tag, "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime()
                .totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime
                ().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime()
                .freeMemory()/1048576)) +"MB free)");

    }
}
