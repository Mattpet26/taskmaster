package com.petersen.taskmaster.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
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
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.petersen.taskmaster.AllTasks;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.Signin;
import com.petersen.taskmaster.Signup;
import com.petersen.taskmaster.TaskDetail;
import com.petersen.taskmaster.ViewAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

    ArrayList<TaskItem> tasks;
    RecyclerView recyclerView;
    ArrayList<Team> teams;
    Handler handler;
    Handler handleSingleItem;
    Handler handlecheckLoggedIn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tasks = new ArrayList<>();

//============================================================================ Handlers ==================================================================
        handler = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        connectAdapterToRecycler();
                        recyclerView.getAdapter().notifyDataSetChanged();
                        return false;
                    }
                });

        handleSingleItem = new Handler(Looper.getMainLooper(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        recyclerView.getAdapter().notifyItemInserted(tasks.size() - 1);
                        return false;
                    }
                });

        handlecheckLoggedIn = new Handler(Looper.getMainLooper(), message -> {
            if (message.arg1 == 0) {
                Log.i("Amplify.login", "They weren't logged in");
                Button logout = MainActivity.this.findViewById(R.id.logout_button);
                logout.setVisibility(View.INVISIBLE);
            } else if (message.arg1 == 1) {
                Log.i("Amplify.login", Amplify.Auth.getCurrentUser().getUsername());
                TextView loggedUser = MainActivity.this.findViewById(R.id.user_loggedin);
                loggedUser.setText(Amplify.Auth.getCurrentUser().getUsername());
                loggedUser.setVisibility(View.VISIBLE);
            } else {
                Log.i("Amplify.login", "Send true or false pls");
            }
            return false;
        });

//============================================================================ onCreate continued ==================================================================
        configureAws();
        getIsSignedIn();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String team = preferences.getString("team", null);

        Amplify.API.query(
                ModelQuery.list(TaskItem.class),
                response -> {
                    for (TaskItem task : response.getData()) {
                        if (preferences.contains("team")) {
                            if (task.getFoundAt().getName().equals(preferences.getString("team", null))) {
                                tasks.add(task);
                            }
                        } else {
                            tasks.add(task);
                        }
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e("Amplify", "Failed to retrieve store")
        );

        String SUBSCRIBETAG = "Amplify.subscription";
        ApiOperation subscription = Amplify.API.subscribe(
                ModelSubscription.onCreate(TaskItem.class),
                onEstablished -> Log.i("Amplify.subscribe", "Subscription established"),
                createdItem -> {
                    Log.i(SUBSCRIBETAG, "Subscription created: " + ((TaskItem) createdItem.getData()).getName()
                    );
                    TaskItem newItem = (TaskItem) createdItem.getData();
                    if (newItem.getFoundAt().getName().equals(preferences.getString("team", null))) {
                        tasks.add(newItem);
                        handleSingleItem.sendEmptyMessage(1);
                    }
                },
                onFailure -> {
                    Log.i(SUBSCRIBETAG, onFailure.toString());
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
        Button goToSettingsButton = MainActivity.this.findViewById(R.id.setting_button);
        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettingsButton = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(goToSettingsButton);
            }
        });

//================================================================ take user to add task =======================================================================================
        Button goToAddTask = MainActivity.this.findViewById(R.id.add_task);
        goToAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

//============================================================== Direct Login =======================================================================================
        Button signUp = MainActivity.this.findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, Signup.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

//============================================================== Direct SignIn =======================================================================================
        Button login = MainActivity.this.findViewById(R.id.sign_in_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddTask = new Intent(MainActivity.this, Signin.class);
                MainActivity.this.startActivity(goToAddTask);
            }
        });

//============================================================== Direct logout =======================================================================================
        Button logout = MainActivity.this.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutButton = new Intent(MainActivity.this, Signin.class);
                MainActivity.this.startActivity(logoutButton);

                TextView loggedUser = MainActivity.this.findViewById(R.id.user_loggedin);
                loggedUser.setText(Amplify.Auth.getCurrentUser().getUsername());
                loggedUser.setVisibility(View.INVISIBLE);

                Button logout = MainActivity.this.findViewById(R.id.logout_button);
                logout.setVisibility(View.INVISIBLE);

                Amplify.Auth.signOut(
                        AuthSignOutOptions.builder().globalSignOut(true).build(),
                        () -> Log.i("AuthQuickstart", "Signed out globally"),
                        error -> Log.e("AuthQuickstart", error.toString())
                );
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
        intent.putExtra("key", taskClass.file);
        this.startActivity(intent);
    }


//============================================================================= Callback functions ================================================================================================

    //======================================================== Recycler
    private void connectAdapterToRecycler() {
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewAdapter(tasks, this));
    }

    //======================================================== Amplify
    private void configureAws() {
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }
    }

    //====================================================== user signed-in
    public boolean getIsSignedIn() {
        boolean[] isSingedIn = {false};

        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i("Amplify.login", result.toString());
                    Message message = new Message();
                    if (result.isSignedIn()) {
                        message.arg1 = 1;
                        handlecheckLoggedIn.sendMessage(message);
                    } else {
                        message.arg1 = 0;
                        handlecheckLoggedIn.sendMessage(message);
                    }
                },
                error -> Log.e("Amplify.login", error.toString())
        );
        return isSingedIn[0];
    }

//============================================================================ On Resume =================================================================================================
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userTask = findViewById(R.id.user_task_list);
        userTask.setText(preference.getString("username", "My Tasks"));

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewAdapter(tasks, this));
    }
}