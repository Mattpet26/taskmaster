package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

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
//===================================== recycler ========================================================================================================

        ArrayList<TaskClass> tasks = new ArrayList<>();
        tasks.add(new TaskClass("Dishes", "Wash and dry the dishes", "completed"));
        tasks.add(new TaskClass("Vacuum", "Vacuum all carpets in the house", "in progress"));
        tasks.add(new TaskClass("Mow Lawn", "Mow the lawn", "new"));
        tasks.add(new TaskClass("Sweep", "Sweep the floors before vacuuming", "completed"));
        tasks.add(new TaskClass("Cook", "Cook before 6:30 pm", "assigned"));
        tasks.add(new TaskClass("Homework", "Finish code challenges and lab before 7pm", "in progress"));

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewAdapter(tasks, this));

//===================================== 3 hard coded tasks to view =======================================================================================
//TODO: I will delete these three hardcoded tasks when I am sure we will not need them. They may be good reference on a future task as well.

//        Button task_3_button = MainActivity.this.findViewById(R.id.task_3_button);
//        task_3_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView task3 = findViewById(R.id.task3);
//                String taskName = task3.getText().toString();
//
//                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
//                goToDetails.putExtra("taskName", taskName);
//                MainActivity.this.startActivity(goToDetails);
//            }
//        });
//        Button task_2_button = MainActivity.this.findViewById(R.id.task_2_button);
//        task_2_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView task2 = findViewById(R.id.task2);
//                String taskName = task2.getText().toString();
//
//                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
//                goToDetails.putExtra("taskName", taskName);
//                MainActivity.this.startActivity(goToDetails);
//            }
//        });
//
//        Button task_1_button = MainActivity.this.findViewById(R.id.task_1_button);
//        task_1_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TextView task1 = findViewById(R.id.task1);
//                String taskName = task1.getText().toString();
//
//                Intent goToDetails = new Intent(MainActivity.this, TaskDetail.class);
//                goToDetails.putExtra("taskName", taskName);
//                MainActivity.this.startActivity(goToDetails);
//            }
//        });
//===================================== take user to all tasks =======================================================================================

        Button all_tasks = MainActivity.this.findViewById(R.id.all_tasks);
        all_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });
//===================================== take user to settings =======================================================================================

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
//===================================== task listener ================================================================================================

    @Override
    public void taskListener(TaskClass taskClass){
        Intent intent = new Intent(MainActivity.this, TaskDetail.class);
        intent.putExtra("title", taskClass.title);
        intent.putExtra("description", taskClass.description);
        intent.putExtra("state", taskClass.state);
        this.startActivity(intent);
    }
}