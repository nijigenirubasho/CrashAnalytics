package zqy.gp.crashanalytics.work;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import zqy.gp.crashanalytics.data.MainDatabase;
import zqy.gp.crashanalytics.data.TempRecorder;
import zqy.gp.crashanalytics.data.bean.EnvironmentInfo;
import zqy.gp.crashanalytics.data.bean.ExceptionInfo;

public class StoreWorker extends Worker {

    private static final String TAG = "StoreWorker";

    public static String TAG_DATA_EXCEPTION = "exception";
    public static String TAG_DATA_TIMESTAMP = "timestamp";
    public static String TAG_DATA_CUSTOM = "custom";

    public StoreWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void call(@NonNull Context context, String exception, boolean isCustom, long timestamp) {
        Data data = new Data.Builder()
                .putString(TAG_DATA_EXCEPTION, exception)
                .putBoolean(TAG_DATA_CUSTOM, isCustom)
                .putLong(TAG_DATA_TIMESTAMP, timestamp)
                .build();
        WorkRequest request = new OneTimeWorkRequest.Builder(StoreWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(request);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork");
        saveException();
        return Result.success();
    }

    private void saveException() {
        Data data = getInputData();
        Context context = getApplicationContext();

        ExceptionInfo info = new ExceptionInfo();
        String pkgName = context.getPackageName();
        info.setPackageName(pkgName);
        try {
            info.setVersionName(context.getPackageManager().getPackageInfo(pkgName, 0).versionName);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        info.setCustom(data.getBoolean(TAG_DATA_CUSTOM, false));
        info.setTimestamp(data.getLong(TAG_DATA_TIMESTAMP, info.getTimestamp()));
        info.setStacktrace(data.getString(TAG_DATA_EXCEPTION));

        info.setActivityTraceList(TempRecorder.getActivity());
        info.setCustomLogList(TempRecorder.getLog());
        info.setProperty(TempRecorder.getProperty());

        EnvironmentInfo envInfo = new EnvironmentInfo();
        envInfo.setModel(Build.MODEL);
        envInfo.setManufacturer(Build.MANUFACTURER);
        envInfo.setSdkInt(Build.VERSION.SDK_INT);
        envInfo.setFingerprint(Build.FINGERPRINT);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memInfo);
        envInfo.setAvailableMemory(memInfo.availMem);
        envInfo.setAvailableStorage(new StatFs(context.getExternalFilesDir(null).getPath()).getAvailableBytes());
        envInfo.setOrientation(context.getResources().getConfiguration().orientation);
        info.setEnvironmentInfo(envInfo);

        MainDatabase.getInstance(context).exceptionInfoDao().insert(info);
    }
}
