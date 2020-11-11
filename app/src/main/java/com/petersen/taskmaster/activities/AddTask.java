package com.petersen.taskmaster.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.petersen.taskmaster.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddTask extends AppCompatActivity {

    ArrayList<Team> teams;
    String globalKey;
    File fileCopy;
    Location currentLocation;
    FusedLocationProviderClient locationProviderClient;
    String addressString;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        parseIntentFilter();
        askForPermission();
        configureLocationServices();
        askLocation();

        teams = new ArrayList<>();
        Amplify.API.query(
                ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        teams.add(team);
                    }
                    Log.i("Amplify.queryitems", "TeamAdded");
                },
                error -> Log.e("Amplify.queryitems", "Didn't get a team!")
        );

//================================================= Add-Task ======================================================================================
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button add_task_button = AddTask.this.findViewById(R.id.add_task_button);
        add_task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemNameInput = AddTask.this.findViewById(R.id.editTaskTitle);
                EditText itemDescriptionInput = AddTask.this.findViewById(R.id.editTaskDescription);
                EditText itemState = AddTask.this.findViewById(R.id.editTaskState);

                String taskName = itemNameInput.getText().toString();
                String description = itemDescriptionInput.getText().toString();
                String state = itemState.getText().toString();

                RadioGroup teamRadGroup = AddTask.this.findViewById(R.id.radioGroup);
                RadioButton selectedTeam = AddTask.this.findViewById(teamRadGroup.getCheckedRadioButtonId());

                AnalyticsEvent event = AnalyticsEvent.builder()
                        .name("added a task")
                        .addProperty("time", Long.toString(new Date().getTime()))
                        .addProperty("we can check if users add tasks! ", "we like tracking people")
                        .build();
                Amplify.Analytics.recordEvent(event);

                String teamName = selectedTeam.getText().toString();
                System.out.println(teamName);

                Team teamSelected = null;
                for (int i = 0; i < teams.size(); i++) {
                    if (teams.get(i).getName().equals(teamName)) {
                        teamSelected = teams.get(i);
                    }
                }

                if (fileCopy.exists()) {
                    globalKey = fileCopy.getName() + Math.random();
                    uploadFile(fileCopy, globalKey);
                }

//================================================= Amplify Add-Task =======================================================================================
                TaskItem taskClass;
                taskClass = TaskItem.builder()
                        .name(taskName)
                        .description(description)
                        .state(state)
                        .foundAt(teamSelected)
                        .file(globalKey)
                        .location(addressString)
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(taskClass),
                        response -> Log.i("AddTaskAmplify", "Your task was saved, you saved ---- " + taskName + " ----"),
                        error -> Log.e("Amplify", error.toString()));

                System.out.println(String.format("task title is %s , description is %s", taskName, description));
                TextView showSubmit = AddTask.this.findViewById(R.id.show_submit);
                showSubmit.setVisibility(View.VISIBLE);
            }
        });

//================================================================ Pictures ======================================================================================================
        Button getPics = AddTask.this.findViewById(R.id.add_image);
        getPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveFile();
            }
        });
    }

    //================================================================= S3
    private void uploadFile(File f, String key) {
        Amplify.Storage.uploadFile(
                key,
                f,
                result -> {
                    Log.i("Amplify.s3", "Successfully uploaded: " + result.getKey());
                    downloadFile(key);
                },
                storageFailure -> Log.e("Amplify.s3", "Upload failed", storageFailure)
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99) {
            Log.i("Amplify.pickImage", "Got the image back from the activity");

            fileCopy = new File(getFilesDir(), "test file");

            try {
                InputStream inStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inStream, new FileOutputStream(fileCopy));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Amplify.pickImage", e.toString());
            }

            uploadFile(fileCopy, fileCopy.getName() + Math.random());
        } else {
            Log.i("Amplify.pickImage", "How the heck are you talking to my app??????");
        }
    }

    private void downloadFile(String fileKey) {
        Amplify.Storage.downloadFile(
                fileKey,
                new File(getApplicationContext().getFilesDir() + "/" + fileKey + ".txt"),
                result -> {
                    Log.i("Amplify.s3down", "Successfully downloaded: " + result.getFile().getName());
                    ImageView image = findViewById(R.id.imageView);
                    image.setImageBitmap(BitmapFactory.decodeFile(result.getFile().getPath()));
                },
                error -> Log.e("Amplify.s3down", "Download Failure", error)
        );
    }

    public void retrieveFile() {
        Intent getPicture = new Intent(Intent.ACTION_GET_CONTENT);
        getPicture.setType("*/*");
        startActivityForResult(getPicture, 99);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void parseIntentFilter() {
        Intent intent = getIntent();
        if (intent.getType() != null && intent.getType().equals("image/jpeg")) {
            intent.getStringExtra(Intent.ACTION_GET_CONTENT);
            Uri imgData = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            fileCopy = new File(getFilesDir(), "test file");


            try {
                InputStream inStream = getContentResolver().openInputStream(imgData);
                FileUtils.copy(inStream, new FileOutputStream(fileCopy));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Amplify.pickImage", e.toString());
            }
            ImageView image = findViewById(R.id.imageView);
            image.setImageURI(imgData);

            Log.i("retrieved an image", intent.getType() + "=========================================================");
        }
    }

    //======================================================= location
    public void configureLocationServices() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void askLocation() {
        LocationRequest locationRequest;
        LocationCallback locationCallback;

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                currentLocation = locationResult.getLastLocation();
                Log.i("Amplify_location", currentLocation.toString());

                Geocoder geocoder = new Geocoder(AddTask.this, Locale.getDefault());
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    public void askForPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    //=============================================== options =======================================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(AddTask.this, MainActivity.class);
        AddTask.this.startActivity(intent);
        return true;
    }
}