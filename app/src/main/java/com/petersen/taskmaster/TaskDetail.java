package com.petersen.taskmaster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.petersen.taskmaster.activities.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class TaskDetail extends AppCompatActivity {

    String lastFileUploaded;

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
        String lastFileUploaded = intent.getStringExtra("key");
        downloadFile(lastFileUploaded);

        System.out.println(intent.getExtras().getString("taskName"));

        System.out.println(intent.getExtras().getString("keyId"));
        System.out.println(intent.getExtras().getString("keyName"));


        TextView taskTitle = TaskDetail.this.findViewById(R.id.task_name_detail);
        taskTitle.setText(intent.getExtras().getString("title") + " details:");

        TextView taskDetail = TaskDetail.this.findViewById(R.id.task_description);
        taskDetail.setText(intent.getExtras().getString("description"));

        TextView taskState = TaskDetail.this.findViewById(R.id.task_state);
        taskState.setText(intent.getExtras().getString("state"));
    }
    //========================================================== S3 ===================================================================================================
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
}