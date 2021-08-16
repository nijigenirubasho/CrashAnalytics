package zqy.gp.crashanalytics.data.bean;

import androidx.annotation.Keep;

@Keep
public class CustomLog extends Timestamp {

    private final String msg;

    public CustomLog(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
