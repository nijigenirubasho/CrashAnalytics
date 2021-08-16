package zqy.gp.crashanalytics.catcher;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import zqy.gp.crashanalytics.util.AppUtils;
import zqy.gp.crashanalytics.work.StoreWorker;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";

    private boolean mCustom;
    private long mTimestamp;

    public void uncaughtCustomException(@NonNull Throwable e) {
        mTimestamp = System.currentTimeMillis();
        mCustom = true;
        uncaughtException(Thread.currentThread(), e);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        mTimestamp = System.currentTimeMillis();
        Log.d(TAG, "uncaughtException");

        StoreWorker.call(AppUtils.getApplication(), Log.getStackTraceString(e), mCustom, mTimestamp);

        if (!mCustom) {
            SystemClock.sleep(2000);
            AppUtils.restart();
        }
    }
}
