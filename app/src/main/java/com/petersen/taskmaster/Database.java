package com.petersen.taskmaster;

import androidx.room.RoomDatabase;

import com.amplifyframework.datastore.generated.model.TaskItem;

@androidx.room.Database(entities = {TaskItem.class}, version = 2)
public abstract class Database extends RoomDatabase {
    public abstract TaskItemDao taskItemDao();
}
