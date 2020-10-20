package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userTask = findViewById(R.id.user_task_list);
        userTask.setText(preference.getString("username", "My Tasks"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//===================================== 3 hard coded tasks to view =======================================================================================
        Button task_3_button = MainActivity.this.findViewById(R.id.task_3_button);
        task_3_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView task3 = findViewById(R.id.task3);
                String taskName = task3.getText().toString();

                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
                goToDetails.putExtra("taskName", taskName);
                MainActivity.this.startActivity(goToDetails);
            }
        });
        Button task_2_button = MainActivity.this.findViewById(R.id.task_2_button);
        task_2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView task2 = findViewById(R.id.task2);
                String taskName = task2.getText().toString();

                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
                goToDetails.putExtra("taskName", taskName);
                MainActivity.this.startActivity(goToDetails);
            }
        });

        Button task_1_button = MainActivity.this.findViewById(R.id.task_1_button);
        task_1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView task1 = findViewById(R.id.task1);
                String taskName = task1.getText().toString();

                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
                goToDetails.putExtra("taskName", taskName);
                MainActivity.this.startActivity(goToDetails);
            }
        });
//===================================== take user to all tasks =======================================================================================
        Button all_tasks = MainActivity.this.findViewById(R.id.all_tasks);
        all_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });
//===================================== take usser to settings =======================================================================================
        final Button goToSettingsButton = MainActivity.this.findViewById(R.id.setting_button);
        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettingsButton = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(goToSettingsButton);
            }
        });
//===================================== take user to add task =======================================================================================

        Button add_task = MainActivity.this.findViewById(R.id.add_task);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });
    }
}