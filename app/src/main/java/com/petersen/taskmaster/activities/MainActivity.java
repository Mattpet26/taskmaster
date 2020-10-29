package com.petersen.taskmaster.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.amplifyframework.api.ApiOperation;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.petersen.taskmaster.AllTasks;
//import com.petersen.taskmaster.database.Database;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.TaskDetail;
import com.petersen.taskmaster.ViewAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

//    Database db;
    ArrayList<TaskItem> tasks;
    RecyclerView recyclerView;
    ArrayList<Team> teams;
    Handler handler;
    Handler handleSingleItem;
    int storeWeAreOnIndex = 0;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userTask = findViewById(R.id.user_task_list);
        userTask.setText(preference.getString("username", "My Tasks"));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        connectAdapterToRecycler();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return true;
                    }
                });

        handleSingleItem = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        recyclerView.getAdapter().notifyItemInserted(tasks.size() - 1);
                        return true;
                    }
                });

        configureAws(handler);
//        configureDB();

        String SUBSCRIBETAG = "Amplify.subscription";
        ApiOperation subscription = Amplify.API.subscribe(
                ModelSubscription.onCreate(TaskItem.class),
                onEstablished -> Log.i("Amplify.subscribe", "Subscription established"),
                createdItem -> {
                    Log.i(SUBSCRIBETAG, "Subscription created: " + ((TaskItem) createdItem.getData()).getName()
                    );
                    TaskItem newItem = (TaskItem) createdItem.getData();
                    tasks.add(newItem);
                    handleSingleItem.sendEmptyMessage(1);
                },
                onFailure -> {
                    Log.i(SUBSCRIBETAG, "Subscription failed");
                },
                () -> Log.i(SUBSCRIBETAG, "Subscription completed")
        );

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
//============================================================================ Stores =================================================================================================

    public void storeCreation() {
        Team team1 = Team.builder()
                .address("123 sus road")
                .name("Red")
                .build();
        Team team2 = Team.builder()
                .address("111 Eleven Rd. drive")
                .name("Blue")
                .build();
        Team team3 = Team.builder()
                .address("42 Wallaby Way, Sydney, Australia")
                .name("Green")
                .build();

        Amplify.API.mutate(ModelMutation.create(team1),
                response -> Log.i("Amplify", "Added a store"),
                error -> Log.e("Amplify", "Failed to add a store")
        );

        Amplify.API.mutate(ModelMutation.create(team2),
                response -> Log.i("Amplify", "Added a store"),
                error -> Log.e("Amplify", "Failed to add a store")
        );

        Amplify.API.mutate(ModelMutation.create(team3),
                response -> Log.i("Amplify", "Added a store"),
                error -> Log.e("Amplify", "Failed to add a store")
        );
    }

//============================================================================= Methods ================================================================================================

//======================================================== Recycler
    private void connectAdapterToRecycler() {
        tasks = new ArrayList<TaskItem>();
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        for(TaskItem item : teams.get(storeWeAreOnIndex).getTaskItems()){
//            tasks.add(item);
//        }
        recyclerView.setAdapter(new ViewAdapter(tasks, this));
    }

//======================================================== Amplify
    private void configureAws(Handler handler){
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Amplify.API.query(
                    ModelQuery.list(Team.class),
                    response -> {
//                                for(Team team : response.getData()) {
//                                    teams.add(team);
//                                }
                        handler.sendEmptyMessage(1);
                    },
                    error -> Log.e("Amplify", "Failed to retrieve store")
                    );
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

//========================================================= Database
//    private void configureDB(){
//        db = Room.databaseBuilder(getApplicationContext(), Database.class, "matthew_task_database")
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();
//    }
}