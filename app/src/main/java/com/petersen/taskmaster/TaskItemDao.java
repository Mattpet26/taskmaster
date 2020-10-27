package com.petersen.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.amplifyframework.datastore.generated.model.TaskItem;
import java.util.List;

@Dao
public interface TaskItemDao {
    @Insert
    public void save(TaskItem taskItem);

    @Query("SELECT * FROM TaskItem")
    public List<TaskItem> getAllTasks();

    @Query("SELECT * FROM TaskItem ORDER BY id DESC")
    public List<TaskItem> getAllTasksReversed();
}
