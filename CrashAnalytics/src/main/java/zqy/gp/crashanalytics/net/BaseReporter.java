package zqy.gp.crashanalytics.net;

import java.io.IOException;

import zqy.gp.crashanalytics.data.bean.ExceptionInfo;

public interface BaseReporter {

    boolean onReport(ExceptionInfo exceptionInfo) throws IOException;
}
