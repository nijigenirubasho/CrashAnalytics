package zqy.gp.crashanalytics.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import zqy.gp.crashanalytics.data.bean.ExceptionInfo;
import zqy.gp.crashanalytics.data.dao.ExceptionInfoDao;

@Database(entities = {ExceptionInfo.class}, version = 1, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase {

    private static final String TAG = "MainDatabase";

    private static MainDatabase mainDatabase;
    private static final String DB_NAME = "crash_analytics.db";

    public abstract ExceptionInfoDao exceptionInfoDao();

    public static MainDatabase getInstance(Context context) {
        if (mainDatabase == null) {
            Log.d(TAG, "getInstance: create.");
            mainDatabase = Room
                    .databaseBuilder(context, MainDatabase.class, DB_NAME)
//                    .allowMainThreadQueries()
                    .build();
        }
        return mainDatabase;
    }
}
