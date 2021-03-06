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
                .addRow("??????", "???")
                .addRow("??????", ConvertUtils.getReadableTimeString(exceptionInfo.getTimestamp()))
//                .addRow("???????????????", exceptionInfo.isCustom())
                .addRow("SDK??????", new BoldText(envInfo.getSdkInt()))
                .addRow("??????", new BoldText(envInfo.getManufacturer()))
                .addRow("??????", new BoldText(envInfo.getModel()))
                .addRow("??????", envInfo.getFingerprint())
                .addRow("??????????????????", ConvertUtils.sizeValueToString(envInfo.getAvailableMemory()))
                .addRow("??????????????????", ConvertUtils.sizeValueToString(envInfo.getAvailableStorage()))
                .addRow("????????????", envInfo.getOrientation() == Configuration.ORIENTATION_PORTRAIT ? "??????" : "??????")
                .build();

        Table.Builder traceBuilder = new Table.Builder().withAlignment(Table.ALIGN_LEFT);
        traceBuilder.addRow("??????", "Activity", "????????????");
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
                .addRow("???", "???");
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
        sb.append(new Heading("????????????", 1)).append("\n")
                .append(new Heading("?????????????????????" + Arrays.asList(
                        "???????????????" + exceptionInfo.getPackageName(),
                        "???????????????" + exceptionInfo.getVersionName(),
                        "????????????" + verCode
                ), 3)).append("\n")
                .append(new Heading("??????", 2)).append("\n")
                .append(envInfoTable).append("\n\n")
                .append(new Heading("????????????", 2)).append("\n")
                .append(new CodeBlock(exceptionInfo.getStacktrace(), "Java")).append("\n\n")
                .append(new Heading("Activity??????", 2)).append("\n")
                .append(traceTable).append("\n\n")
                .append(new Heading("???????????????", 2)).append("\n")
                .append(new CodeBlock(logBuilder)).append("\n\n")
                .append(new Heading("???????????????", 2)).append("\n")
                .append(propertyTable).append("\n");

        return sb.toString();
    }
}
