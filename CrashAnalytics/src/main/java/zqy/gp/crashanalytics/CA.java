package zqy.gp.crashanalytics;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import zqy.gp.crashanalytics.catcher.ActivityTracer;
import zqy.gp.crashanalytics.catcher.ExceptionHandler;
import zqy.gp.crashanalytics.data.TempRecorder;
import zqy.gp.crashanalytics.util.AppUtils;
import zqy.gp.crashanalytics.work.ReportWorker;

/**
 * Crash Analytics 公开API
 *
 * @author 曾庆尧
 */
public class CA {

    private CA() {
    }

    private static final String TAG = "CA";

    private static boolean sInitialized;

    /**
     * 初始化SDK，需要尽早调用，建议在{@link Application#onCreate()}中调用
     *
     * @param accessToken Github的personal access Token
     * @param owner       repo持有者名称
     * @param repository  repo库名称
     */
    public static void initialize(String accessToken, String owner, String repository) {
        Application app = AppUtils.getApplication();
        if (sInitialized) {
            Log.i(TAG, "Initialized.");
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        ActivityTracer.register(app);
        ReportWorker.call(app, accessToken, owner, repository);
        sInitialized = true;
    }

    /**
     * 记录自定义日志
     *
     * @param log 日志信息
     */
    public static void log(@NonNull String log) {
        ensureInitialization();
        TempRecorder.putLog(log);
    }

    /**
     * 设置自定义属性键值对
     *
     * @param key   唯一键
     * @param value 值
     */
    public static void setProperty(@NonNull String key, @NonNull String value) {
        ensureInitialization();
        TempRecorder.putProperty(key, value);
    }

    /**
     * 设置自定义属性键值对
     *
     * @param key   唯一键
     * @param value 值
     */
    public static void setProperty(@NonNull String key, long value) {
        setProperty(key, Long.toString(value));
    }

    /**
     * 设置自定义属性键值对
     *
     * @param key   唯一键
     * @param value 值
     */
    public static void setProperty(@NonNull String key, boolean value) {
        setProperty(key, Boolean.toString(value));
    }

    /**
     * 设置自定义属性键值对
     *
     * @param key   唯一键
     * @param value 值
     */
    public static void setProperty(@NonNull String key, float value) {
        setProperty(key, Float.toString(value));
    }

    /**
     * 设置自定义属性键值对
     *
     * @param key   唯一键
     * @param value 值
     */
    public static void setProperty(@NonNull String key, int value) {
        setProperty(key, Integer.toString(value));
    }

    /**
     * 报告自定义异常
     *
     * @param t 异常对象
     */
    public static void onCustomException(@NonNull Throwable t) {
        ensureInitialization();
        new ExceptionHandler().uncaughtCustomException(t);
    }

    /**
     * 制造一次崩溃，测试SDK是否生效
     * 在发布版本，该方法不会生效
     */
    public static void testCrash() {
        if ((AppUtils.getApplication().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0) {
            throw new RuntimeException("Test crash from CrashAnalytics.");
        } else {
            Log.w(TAG, "testCrash: Won't make crash in release build.");
        }
    }

    /**
     * 确保初始化完成，可以进行外部端口调用
     */
    private static void ensureInitialization() {
        if (!sInitialized) throw new IllegalStateException("Crash Analytics not initialized."
                + " Please call initialize() first to initialize CrashAnalytics.");
    }
}
