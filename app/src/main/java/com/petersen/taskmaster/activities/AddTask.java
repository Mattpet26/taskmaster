package com.petersen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.petersen.taskmaster.R;
import com.petersen.taskmaster.Database;

public class AddTask extends AppCompatActivity {

    Database db;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(AddTask.this, MainActivity.class);
        AddTask.this.startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = Room.databaseBuilder(getApplicationContext(), Database.class, "matthew_task_database")
                .allowMainThreadQueries()
                .build();
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

//                TaskClass taskClass = new TaskClass(taskName, description, state);
//                db.taskClassDao().save(taskClass);

//================================================= Amplify Add-Task =======================================================================================

                    TaskItem taskClass;
                    taskClass = TaskItem.builder()
                            .name(taskName)
                            .description(description)
                            .state(state)
                            .build();

                    Amplify.API.mutate(
                            ModelMutation.create(taskClass),
                            response -> Log.i("AddTaskAmplify", "Your task was saved, you saved ---- " + taskName + " ----"),
                            error -> Log.e("Amplify", error.toString()));
                    db.taskItemDao().save(taskClass);


//              tasks.add(0, taskClass);    <---- do this step if the recycler is on the same page as the submit button
//              recyclerView.getAdapter().notifyItemInserted(0);
//              recyclerView.smoothScrollToPosition(0);

                System.out.println(String.format("task title is %s , description is %s", taskName, description));
                TextView showSubmit = AddTask.this.findViewById(R.id.show_submit);
                showSubmit.setVisibility(View.VISIBLE);
            }
        });

    }
}