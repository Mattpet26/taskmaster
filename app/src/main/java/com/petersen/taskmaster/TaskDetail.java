package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(TaskDetail.this, MainActivity.class);
        TaskDetail.this.startActivity(intent);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        System.out.println(intent.getExtras().getString("taskName"));

        TextView taskNameText = TaskDetail.this.findViewById(R.id.task_name_detail);
        taskNameText.setText(intent.getExtras().getString("taskName") + " details:");
    }
}