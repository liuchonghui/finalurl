package tools.android.finalurl;

import android.util.Log;

class LogUtil {

    public static boolean enableLogcat = false;

    public static void enableLogcat(boolean enable) {
        enableLogcat = enable;
    }

    public static void d(String tag, String input) {
        if (!enableLogcat) {
            return;
        }
        Log.d(tag, input);
    }
}
