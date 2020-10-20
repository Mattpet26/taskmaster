package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Button add_task_button = AddTask.this.findViewById(R.id.add_task_button);
        add_task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemNameInput = AddTask.this.findViewById(R.id.editTaskTitle);
                EditText itemDescriptionInput = AddTask.this.findViewById(R.id.editTaskDescription);

                String taskName = itemNameInput.getText().toString();
                String description = itemDescriptionInput.getText().toString();

                System.out.println(String.format("task title is %s , description is %s", taskName, description));

                TextView showSubmit = AddTask.this.findViewById(R.id.show_submit);
                showSubmit.setVisibility(View.VISIBLE);
//                Intent intent = getIntent();
//                intent.putExtra("title", taskName);
//                intent.putExtra("description", description);

//                TextView task_name = AddTask.this.findViewById(R.id.new_task_name);
//                task_name.setText(intent.getExtras().getString("title"));
            }
        });
    }
}