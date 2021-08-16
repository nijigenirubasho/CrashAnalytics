package zqy.gp.crashanalytics.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConvertUtils {

    private ConvertUtils() {
    }

    /**
     * @see android.util.DebugUtils
     */
    @SuppressLint("DefaultLocale")
    public static String sizeValueToString(long number) {
        StringBuilder outBuilder = new StringBuilder();
        float result = number;
        String suffix = "";
        if (result > 900) {
            suffix = "KB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "MB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "GB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "TB";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "PB";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.1f", result);
        } else if (result < 100) {
            value = String.format("%.0f", result);
        } else {
            value = String.format("%.0f", result);
        }
        outBuilder.append(value);
        outBuilder.append(suffix);
        return outBuilder.toString();
    }

    public static String getReadableTimeString(long time) {
        String TIME_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS z";
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date(time));
    }
}
