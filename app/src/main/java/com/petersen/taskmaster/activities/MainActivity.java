package com.petersen.taskmaster.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.petersen.taskmaster.AllTasks;
import com.petersen.taskmaster.Database;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.TaskDetail;
import com.petersen.taskmaster.ViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

    Database db;
    ArrayList<TaskItem> tasks;
    RecyclerView recyclerView;

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

        Handler handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

        configureAws();
        configureDB();
        connectAdapterToRecycler(handler);

//============================================================== Direct All-Tasks =======================================================================================

        Button all_tasks = MainActivity.this.findViewById(R.id.all_tasks);
        all_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });

//================================================================== Direct Settings =======================================================================================

        final Button goToSettingsButton = MainActivity.this.findViewById(R.id.setting_button);
        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettingsButton = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(goToSettingsButton);
            }
        });

//================================================================ take user to add task =======================================================================================

        Button add_task = MainActivity.this.findViewById(R.id.add_task);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });
    }

//==================================================================== task listener ================================================================================================

    @Override
    public void taskListener(TaskItem taskClass) {
        Intent intent = new Intent(MainActivity.this, TaskDetail.class);
        intent.putExtra("title", taskClass.name);
        intent.putExtra("description", taskClass.description);
        intent.putExtra("state", taskClass.state);
        this.startActivity(intent);
    }

//============================================================================= Methods ================================================================================================

//======================================================== Recycler
private void connectAdapterToRecycler(Handler handler) {
    tasks = new ArrayList<TaskItem>();
    recyclerView = findViewById(R.id.recycler_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new ViewAdapter(tasks, this));

        Amplify.API.query(
                ModelQuery.list(TaskItem.class),
                response -> {
                    for(TaskItem item : response.getData()){
                        tasks.add(item);
                    }
                    handler.sendEmptyMessage(1);
                    Log.i("Amplify.queryitems", "Got this many items from dynamo " + tasks.size());
                },
                error -> Log.i("Amplify.queryitems", "Did not get items")
        );
//    tasks = (ArrayList<TaskItem>) db.taskItemDao().getAllTasksReversed();


}

//======================================================== Amplify
    private void configureAws(){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MainActivityAmplify", "Initialized Amplify");

//            TaskItem taskitem;
//            taskitem = TaskItem.builder()
//                    .name("Feed")
//                    .description("Feed the fishies")
//                    .state("new")
//                    .build();
//            Amplify.API.mutate(
//                    ModelMutation.create(taskitem),
//                    response -> Log.i("MainActivityAmplify", "task saved!"),
//                    error -> Log.e("Amplify", error.toString())
//            );
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

//========================================================= Database
    private void configureDB(){
        db = Room.databaseBuilder(getApplicationContext(), Database.class, "matthew_task_database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
}