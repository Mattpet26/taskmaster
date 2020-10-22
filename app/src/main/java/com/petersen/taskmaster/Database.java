package com.petersen.taskmaster;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {TaskClass.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract TaskClassDao taskClassDao();
}
