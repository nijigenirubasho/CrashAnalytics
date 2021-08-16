package zqy.gp.crashanalytics.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.InvocationTargetException;

public class AppUtils {

    private AppUtils() {
    }

    @SuppressLint("PrivateApi")
    public static Application getApplication() {
        Application app = new Application();
        try {
            app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
        }
        return app;
    }

    public static void restart() {
        Context context = getApplication();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        System.exit(0);
    }
}
