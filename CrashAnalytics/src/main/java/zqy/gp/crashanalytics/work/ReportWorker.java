package zqy.gp.crashanalytics.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.util.List;

import zqy.gp.crashanalytics.data.MainDatabase;
import zqy.gp.crashanalytics.data.bean.ExceptionInfo;
import zqy.gp.crashanalytics.data.dao.ExceptionInfoDao;
import zqy.gp.crashanalytics.net.github.GithubReporter;

public class ReportWorker extends Worker {

    private static final String TAG = "ReportWorker";

    public static String TAG_DATA_TOKEN = "token";
    public static String TAG_DATA_OWNER = "owner";
    public static String TAG_DATA_REPO = "repo";

    public ReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static void call(@NonNull Context context, String accessToken, String owner, String repository) {
        Data data = new Data.Builder()
                .putString(TAG_DATA_TOKEN, accessToken)
                .putString(TAG_DATA_OWNER, owner)
                .putString(TAG_DATA_REPO, repository)
                .build();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 定义网络连接后才执行工作
                .build();
        WorkRequest request = new OneTimeWorkRequest.Builder(ReportWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(request);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork");
        ExceptionInfoDao infoDao = MainDatabase.getInstance(getApplicationContext()).exceptionInfoDao();
        List<ExceptionInfo> infoList = infoDao.getAll();
        Data data = getInputData();
        for (ExceptionInfo info : infoList) {
            boolean result = false;
            try {
                result = new GithubReporter(
                        data.getString(TAG_DATA_TOKEN),
                        data.getString(TAG_DATA_OWNER),
                        data.getString(TAG_DATA_REPO))
                        .onReport(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result) {
                Log.d(TAG, "doWork: Upload finished! Deleting #" + info.getId());
                infoDao.delete(info);
            }
        }
        final int size = infoList.size();
        if (size == 0) {
            Log.d(TAG, "doWork: No exception info stored. Nothing to do.");
            return Result.success();
        } else {
            Log.d(TAG, "doWork: Has " + size + " info(s). Retry...");
            return Result.retry();
        }
    }
}
