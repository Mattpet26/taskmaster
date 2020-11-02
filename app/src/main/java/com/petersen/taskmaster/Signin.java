package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;
import com.petersen.taskmaster.activities.MainActivity;

public class Signin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button login = findViewById(R.id.loginButton);
        login.setOnClickListener(view ->{

            EditText username = Signin.this.findViewById(R.id.username);
            EditText password = Signin.this.findViewById(R.id.password);

            String userUsername = username.getText().toString();
            String userPassword = password.getText().toString();

        Amplify.Auth.signIn(
                userUsername,
                userPassword,
                result -> {
                    Log.i("Amplify.login", "Sign in succeeded! " + result.toString());
                    startActivity(new Intent(Signin.this, MainActivity.class));
                },
                error -> Log.e("Amplify.login", "Error " + error.toString())
            );
        });
    }
}