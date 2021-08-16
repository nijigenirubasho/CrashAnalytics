package zqy.gp.crashanalytics.data.bean;

import androidx.annotation.Keep;

@Keep
public class Timestamp {

    private long timestamp;

    public Timestamp() {
        timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
