package com.petersen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskItem;
import com.amplifyframework.datastore.generated.model.Team;
import com.petersen.taskmaster.R;

import java.util.ArrayList;

public class AddTask extends AppCompatActivity {

//    Database db;
    ArrayList<Team> teams;
    private RadioGroup radioTeamGroup;
    private RadioButton radioTeamButton;

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

                int selectedId = radioTeamGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(AddTask.this,
                            "No answer has been selected",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    RadioButton radioButton
                            = (RadioButton)radioTeamGroup
                            .findViewById(selectedId);
                }
//================================================= Amplify Add-Task =======================================================================================

                    TaskItem taskClass;
                    taskClass = TaskItem.builder()
                            .name(taskName)
                            .description(description)
                            .state(state)
                            .foundAt(radioTeamGroup.getId(selectedId))
                            .build();

                    Amplify.API.mutate(
                            ModelMutation.create(taskClass),
                            response -> Log.i("AddTaskAmplify", "Your task was saved, you saved ---- " + taskName + " ----"),
                            error -> Log.e("Amplify", error.toString()));
//                    db.taskItemDao().save(taskClass);

                System.out.println(String.format("task title is %s , description is %s", taskName, description));
                TextView showSubmit = AddTask.this.findViewById(R.id.show_submit);
                showSubmit.setVisibility(View.VISIBLE);
            }
        });
//========================================================== Radio Buttons ========================================================================================

        RadioButton button1 = AddTask.this.findViewById(R.id.radioButton1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team teams;
                teams = Team.builder()
                        .name("Red")
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(teams),
                        response -> Log.i("AddTaskAmplify", "You chose RED team!"),
                        error -> Log.e("Amplify", error.toString()));

                System.out.println("Red team was chosen");
            }
        });
        RadioButton button2 = AddTask.this.findViewById(R.id.radioButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team teams;
                teams = Team.builder()
                        .name("Blue")
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(teams),
                        response -> Log.i("AddTaskAmplify", "You chose BLUE team!"),
                        error -> Log.e("Amplify", error.toString()));

                System.out.println("Blue team was chosen");
            }
        });
        RadioButton button3 = AddTask.this.findViewById(R.id.radioButton3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Team teams;
                teams = Team.builder()
                        .name("Green")
                        .build();

                Amplify.API.mutate(
                        ModelMutation.create(teams),
                        response -> Log.i("AddTaskAmplify", "You chose GREEN team!"),
                        error -> Log.e("Amplify", error.toString()));

                System.out.println("Green team was chosen");
            }
        });
    }
}