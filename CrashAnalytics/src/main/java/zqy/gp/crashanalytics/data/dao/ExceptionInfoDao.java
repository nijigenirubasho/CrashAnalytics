package zqy.gp.crashanalytics.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import zqy.gp.crashanalytics.data.bean.ExceptionInfo;

@Dao
public interface ExceptionInfoDao {

    @Query("select * from exception_info")
    List<ExceptionInfo> getAll();

    @Insert
    void insert(ExceptionInfo info);

    @Delete
    void delete(ExceptionInfo info);
}
