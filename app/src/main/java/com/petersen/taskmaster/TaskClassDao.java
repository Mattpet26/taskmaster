package com.petersen.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskClassDao {

    @Insert
    public void save(TaskClass taskClass);

    @Query("SELECT * FROM TaskClass")
    public List<TaskClass> getAllTasks();

    @Query("SELECT * FROM TaskClass ORDER BY id DESC")
    public List<TaskClass> getAllTasksReversed();

}
