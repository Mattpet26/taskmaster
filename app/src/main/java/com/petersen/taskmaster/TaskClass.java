package com.petersen.taskmaster;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskClass {

    @PrimaryKey(autoGenerate = true)
    long id;

    public String title;
    public String description;
    public String state;

    public TaskClass(String title, String description, String state) {
        this.title = title;
        this.description = description;
        this.state = state;
    }
}
