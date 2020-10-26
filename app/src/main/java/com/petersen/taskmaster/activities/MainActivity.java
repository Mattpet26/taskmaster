package com.petersen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.petersen.taskmaster.AllTasks;
import com.petersen.taskmaster.Database;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.TaskDetail;
import com.petersen.taskmaster.ViewAdapter;
import com.petersen.taskmaster.models.TaskClass;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

    Database db;

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

        db = Room.databaseBuilder(getApplicationContext(), Database.class, "matthew_task_database")
                .allowMainThreadQueries()
                .build();

//========================================================== Amplify =================================================================================

//        try{
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//
//            Log.i("MyAmplifyApp", "Initialized Amplify");
//
//            taskItem taskitem;
//            taskitem = taskitem.builder()
//                    .thingName("task1")
//                    .description("do the task1 man")
//                    .state("new")
//                    .build()
//            Amplify.API.mutate(
//                    ModelMutation.create(taskitem),
//                    response -> Log.i("MainActivityAmplify", "task1 saved!"),
//                    error -> Log.e("MainActivityAmplify", "Oops there was an error with task1")
//            );
//        }catch(AmplifyException error){
//            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
//        }


//===================================== recycler falls into onCreate =================================================================================

        ArrayList<TaskClass> tasks = (ArrayList<TaskClass>) db.taskClassDao().getAllTasksReversed();

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewAdapter(tasks, this));

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