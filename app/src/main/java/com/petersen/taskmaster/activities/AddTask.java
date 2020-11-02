package com.petersen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;


public class AddTask extends AppCompatActivity {

    ArrayList<Team> teams;
    private RadioGroup radioTeamGroup;

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
    }

//=============================================== options =======================================================================================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(AddTask.this, MainActivity.class);
        AddTask.this.startActivity(intent);
        return true;
    }
}