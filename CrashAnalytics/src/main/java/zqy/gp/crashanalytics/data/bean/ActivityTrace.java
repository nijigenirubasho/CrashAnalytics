package zqy.gp.crashanalytics.data.bean;

import android.util.Pair;

import androidx.annotation.Keep;

@Keep
public class ActivityTrace extends Timestamp {

    private final Pair<String, String> node;

    public ActivityTrace(String activity, String method) {
        node = Pair.create(activity, method);
    }

    public String getActivity() {
        return node.first;
    }

    public String getMethod() {
        return node.second;
    }
}
