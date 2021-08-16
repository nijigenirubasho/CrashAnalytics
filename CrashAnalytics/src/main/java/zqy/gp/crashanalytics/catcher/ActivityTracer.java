package zqy.gp.crashanalytics.catcher;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import zqy.gp.crashanalytics.data.TempRecorder;

import static android.os.Build.VERSION_CODES.N;

public class ActivityTracer {

    private static final String TAG = "ActivityTracer";

    private ActivityTracer() {
    }

    public static void register(Application app) {
        Class<?> alc = Application.ActivityLifecycleCallbacks.class;
        app.registerActivityLifecycleCallbacks(
                (Application.ActivityLifecycleCallbacks) Proxy.newProxyInstance(
                        alc.getClassLoader(), new Class[]{alc}, new Handler()));
    }

    private static class Handler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            Object firstArg = args[0];
            if (firstArg instanceof Activity) {
                Activity activity = (Activity) firstArg;
                String activityName = activity.getLocalClassName(), methodName = method.getName();
                boolean isVerbose = Build.VERSION.SDK_INT >= N && method.isDefault();
                String recordMsg = String.valueOf(Arrays.asList(activityName, methodName));
                if (!isVerbose) {
                    TempRecorder.putActivity(activityName, methodName);
                }
                Log.i(TAG, "invoke: " + (isVerbose ? "log only" : "record") + recordMsg);
            }
            return null;
        }
    }
}
