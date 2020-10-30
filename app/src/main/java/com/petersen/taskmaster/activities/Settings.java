package com.petersen.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.petersen.taskmaster.R;

public class Settings extends AppCompatActivity {
    String team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        Button login_button = Settings.this.findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.editUsernameText);
                System.out.println(username.getText().toString());

                RadioGroup radioGroup = Settings.this.findViewById(R.id.radioGroup);
                RadioButton radioButton = Settings.this.findViewById(radioGroup.getCheckedRadioButtonId());
                String team = radioButton.getText().toString();


                preferenceEditor.putString("username", username.getText().toString() + "'s task(s):");
                preferenceEditor.putString("team", team);
                preferenceEditor.apply();

                Intent goHome = new Intent(Settings.this, MainActivity.class);
                Settings.this.startActivity(goHome);
                finish();
            }
        });
    }
    public void onRadioButtonClicked(View view) {
         RadioButton radioButton = (RadioButton) view;
         team = radioButton.getText().toString();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        Settings.this.startActivity(intent);
        return true;
    }
}