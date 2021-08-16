package zqy.gp.crashanalytics.data.bean;

import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import zqy.gp.crashanalytics.data.converter.ActivityTraceConverter;
import zqy.gp.crashanalytics.data.converter.BundleConverter;
import zqy.gp.crashanalytics.data.converter.CustomLogConverter;

@TypeConverters({
        ActivityTraceConverter.class,
        CustomLogConverter.class,
        BundleConverter.class})
@Entity(tableName = "exception_info")
@Keep
public class ExceptionInfo extends Timestamp {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private boolean isCustom;

    @Embedded
    private EnvironmentInfo environmentInfo;
    private List<ActivityTrace> activityTraceList;
    private List<CustomLog> customLogList;
    private Bundle property;
    private String stacktrace;
    private String packageName;
    private String versionName;

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
    }

    public List<ActivityTrace> getActivityTraceList() {
        return activityTraceList;
    }

    public void setActivityTraceList(List<ActivityTrace> activityTraceList) {
        this.activityTraceList = activityTraceList;
    }

    public List<CustomLog> getCustomLogList() {
        return customLogList;
    }

    public void setCustomLogList(List<CustomLog> customLogList) {
        this.customLogList = customLogList;
    }

    public Bundle getProperty() {
        return property;
    }

    public void setProperty(Bundle property) {
        this.property = property;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
