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

import com.petersen.taskmaster.R;

public class Settings extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        Settings.this.startActivity(intent);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor preferenceEditor = preferences.edit();

        Button login_button = Settings.this.findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.editUsernameText);
                System.out.println(username.getText().toString());
                preferenceEditor.putString("username", username.getText().toString() + "'s task(s):");
                preferenceEditor.apply();

                Intent goHome = new Intent(Settings.this, MainActivity.class);
                Settings.this.startActivity(goHome);
            }
        });
        Button store1 = Settings.this.findViewById(R.id.store1);
        store1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(storeWeAreOnIndex > 0){
//                storeWeAreOnIndex --;
//                tasks.clear();
//                for(TaskItem item : stores.get(storeWeAreOnIndex).getCheapItems()){
//                tasks.add(item);
//                recyclerView.getAdapter().notifyDataSetChanged();

                Intent goHome = new Intent(Settings.this, MainActivity.class);
                Settings.this.startActivity(goHome);
            }
        });
    }
}