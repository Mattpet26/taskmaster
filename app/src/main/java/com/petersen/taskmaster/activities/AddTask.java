package com.petersen.taskmaster.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.petersen.taskmaster.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class AddTask extends AppCompatActivity {

    ArrayList<Team> teams;
    private RadioGroup radioTeamGroup;
    String lastFileUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

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

                String teamName = selectedTeam.getText().toString();
                System.out.println(teamName);
                Team teamSelected = null;
                for (int i = 0; i < teams.size(); i++) {
                    if (teams.get(i).getName().equals(teamName)) {
                        teamSelected = teams.get(i);
                    }
                }

//================================================= Amplify Add-Task =======================================================================================
                TaskItem taskClass;
                taskClass = TaskItem.builder()
                        .name(taskName)
                        .description(description)
                        .state(state)
                        .foundAt(teamSelected)
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
        lastFileUploaded = key;
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

        if(requestCode == 99){
            Log.i("Amplify.pickImage", "Got the image back from the activity");

            File fileCopy = new File(getFilesDir(), "test file");

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

    public void retrieveFile(){
        Intent getPicture = new Intent(Intent.ACTION_GET_CONTENT);
        getPicture.setType("*/*");
        startActivityForResult(getPicture, 99);
    }

//=============================================== options =======================================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(AddTask.this, MainActivity.class);
        AddTask.this.startActivity(intent);
        return true;
    }
}