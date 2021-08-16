package zqy.gp.crashanalytics.data;

import android.os.Bundle;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import zqy.gp.crashanalytics.data.bean.ActivityTrace;
import zqy.gp.crashanalytics.data.bean.CustomLog;

public class TempRecorder {

    private static final String LOG_FORMAT = "CrashAnalytics Log: %s -> %s";

    private static final List<CustomLog> sLog = new LinkedList<>();
    private static final List<ActivityTrace> sActivity = new LinkedList<>();
    private static final Bundle sProperty = new Bundle();

    public static void putLog(String log) {
        sLog.add(new CustomLog(log));
    }

    public static void putActivity(String activity, String method) {
//        putLog(String.format(LOG_FORMAT, "activity", Arrays.asList(activity, method)));
        sActivity.add(new ActivityTrace(activity, method));
    }

    public static void putProperty(String key, String value) {
//        putLog(String.format(LOG_FORMAT, "property", Arrays.asList(key, value)));
        sProperty.putString(key, value);
    }

    public static List<CustomLog> getLog() {
        return sLog;
    }

    public static List<ActivityTrace> getActivity() {
        return sActivity;
    }

    public static Bundle getProperty() {
        return sProperty;
    }
}
