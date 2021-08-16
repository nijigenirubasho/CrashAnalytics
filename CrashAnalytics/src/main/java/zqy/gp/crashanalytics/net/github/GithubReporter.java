package zqy.gp.crashanalytics.net.github;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.WorkerThread;

import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import net.steppschuh.markdowngenerator.text.emphasis.BoldText;
import net.steppschuh.markdowngenerator.text.heading.Heading;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Response;
import zqy.gp.crashanalytics.data.bean.ActivityTrace;
import zqy.gp.crashanalytics.data.bean.CustomLog;
import zqy.gp.crashanalytics.data.bean.EnvironmentInfo;
import zqy.gp.crashanalytics.data.bean.ExceptionInfo;
import zqy.gp.crashanalytics.net.BaseReporter;
import zqy.gp.crashanalytics.util.AppUtils;
import zqy.gp.crashanalytics.util.ConvertUtils;

@WorkerThread
public class GithubReporter implements BaseReporter {

    private static final String TAG = "GithubReporter";

    private final String mAccessToken, mOwner, mRepository;

    public GithubReporter(String accessToken, String owner, String repository) {
        Log.i(TAG, "GithubReporter: see repo " + Arrays.asList(owner, repository));
        mAccessToken = accessToken;
        mOwner = owner;
        mRepository = repository;
    }

    @Override
    public boolean onReport(final ExceptionInfo exceptionInfo) throws IOException {
        GithubService service = GithubServiceCreator.create(mAccessToken);
        String brief = exceptionInfo.getStacktrace().split("\\r?\\n")[0];
        Response<ResponseBody> response = service.createIssue(mOwner, mRepository, new Issue(brief, buildMarkdown(exceptionInfo))).execute();
        return response.isSuccessful();
    }

    private String buildMarkdown(ExceptionInfo exceptionInfo) {
        StringBuilder sb = new StringBuilder();
        EnvironmentInfo envInfo = exceptionInfo.getEnvironmentInfo();
        Table envInfoTable = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_LEFT)
                .addRow("项目", "值")
                .addRow("时间", ConvertUtils.getReadableTimeString(exceptionInfo.getTimestamp()))
//                .addRow("自定义异常", exceptionInfo.isCustom())
                .addRow("SDK版本", new BoldText(envInfo.getSdkInt()))
                .addRow("厂商", new BoldText(envInfo.getManufacturer()))
                .addRow("型号", new BoldText(envInfo.getModel()))
                .addRow("指纹", envInfo.getFingerprint())
                .addRow("可用运行内存", ConvertUtils.sizeValueToString(envInfo.getAvailableMemory()))
                .addRow("可用存储空间", ConvertUtils.sizeValueToString(envInfo.getAvailableStorage()))
                .addRow("屏幕方向", envInfo.getOrientation() == Configuration.ORIENTATION_PORTRAIT ? "纵向" : "横向")
                .build();

        Table.Builder traceBuilder = new Table.Builder().withAlignment(Table.ALIGN_LEFT);
        traceBuilder.addRow("时间", "Activity", "生命周期");
        for (ActivityTrace at : exceptionInfo.getActivityTraceList()) {
            traceBuilder.addRow(ConvertUtils.getReadableTimeString(at.getTimestamp()), at.getActivity(), at.getMethod());
        }
        Table traceTable = traceBuilder.build();

        StringBuilder logBuilder = new StringBuilder();
        for (CustomLog log : exceptionInfo.getCustomLogList()) {
            logBuilder
                    .append(ConvertUtils.getReadableTimeString(log.getTimestamp()))
                    .append(": ")
                    .append(log.getMsg())
                    .append(System.lineSeparator());
        }

        Table.Builder propertyBuilder = new Table.Builder()
                .withAlignment(Table.ALIGN_CENTER)
                .addRow("键", "值");
        Bundle property = exceptionInfo.getProperty();
        for (String key : property.keySet()) {
            propertyBuilder.addRow(key, property.get(key));
        }
        Table propertyTable = propertyBuilder.build();
        int verCode = 0;
        try {
            verCode = AppUtils.getApplication().getPackageManager().getPackageInfo(exceptionInfo.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append(new Heading("崩溃报告", 1)).append("\n")
                .append(new Heading("来自应用程序：" + Arrays.asList(
                        "软件包名：" + exceptionInfo.getPackageName(),
                        "版本名称：" + exceptionInfo.getVersionName(),
                        "版本号：" + verCode
                ), 3)).append("\n")
                .append(new Heading("属性", 2)).append("\n")
                .append(envInfoTable).append("\n\n")
                .append(new Heading("堆栈信息", 2)).append("\n")
                .append(new CodeBlock(exceptionInfo.getStacktrace(), "Java")).append("\n\n")
                .append(new Heading("Activity轨迹", 2)).append("\n")
                .append(traceTable).append("\n\n")
                .append(new Heading("自定义日志", 2)).append("\n")
                .append(new CodeBlock(logBuilder)).append("\n\n")
                .append(new Heading("自定义属性", 2)).append("\n")
                .append(propertyTable).append("\n");

        return sb.toString();
    }
}
