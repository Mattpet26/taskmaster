package com.petersen.taskmaster.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.ApiOperation;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.api.graphql.model.ModelSubscription;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.Signin;
import com.petersen.taskmaster.Signup;
import com.petersen.taskmaster.TaskDetail;
import com.petersen.taskmaster.ViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ViewAdapter.OnInteractWithTaskListener {

    private AdView mAdView;
    ArrayList<TaskItem> tasks;
    RecyclerView recyclerView;
    Handler handler;
    Handler handleSingleItem;
    Handler handlecheckLoggedIn;
    Location currentLocation;
    FusedLocationProviderClient locationProviderClient;
    String addressString;

    public static final String TAG = "Amplify";

    private static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            final String token = task.getResult().getToken();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tasks = new ArrayList<>();

        getPinpointManager(getApplicationContext());
        askForPermission();
        configureLocationServices();
        askLocation();

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
        getPinpointManager(getApplicationContext());
        getIsSignedIn();
        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("openedApp")
                .addProperty("time", Long.toString(new Date().getTime()))
                .addProperty("so fun, ", "we like tracking people")
                .build();
        Amplify.Analytics.recordEvent(event);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

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
        Button messages = MainActivity.this.findViewById(R.id.check_messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoMessages = new Intent(MainActivity.this, AddTask.class);
                gotoMessages.putExtra("key", "value");
                MainActivity.this.startActivity(gotoMessages);
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

                AnalyticsEvent event = AnalyticsEvent.builder()
                        .name("signed in")
                        .addProperty("time", Long.toString(new Date().getTime()))
                        .addProperty("a user clicked to sign in ", "we like tracking people")
                        .build();
                Amplify.Analytics.recordEvent(event);

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

                AnalyticsEvent event = AnalyticsEvent.builder()
                        .name("logged out")
                        .addProperty("time", Long.toString(new Date().getTime()))
                        .addProperty("ahhh a user logged out! ", "we like tracking people")
                        .build();
                Amplify.Analytics.recordEvent(event);

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
        intent.putExtra("location", taskClass.location);
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
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(getApplication()));
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

   //==================================================================== logged
   public void configureLocationServices() {
       locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
   }

    public void askLocation() {
        LocationRequest locationRequest;
        LocationCallback locationCallback;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                currentLocation = locationResult.getLastLocation();
                Log.i("Amplify_location", currentLocation.toString());

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 10);
                    addressString = addresses.get(0).getAddressLine(0);
                    Log.i("Amplify address", addressString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission();
            return;
        }
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    public void askForPermission(){
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    //============================================================================ On Resume =================================================================================================
    @Override
    public void onResume() {
        super.onResume();

        RecyclerView recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ViewAdapter(tasks, this));
    }
}