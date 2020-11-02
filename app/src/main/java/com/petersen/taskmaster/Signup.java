package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.petersen.taskmaster.activities.AddTask;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button signup = findViewById(R.id.sign_up_button);
        signup.setOnClickListener(view ->{

            EditText username = Signup.this.findViewById(R.id.username);
            EditText password = Signup.this.findViewById(R.id.password);
            EditText email = Signup.this.findViewById(R.id.email);

            String userUsername = username.getText().toString();
            String userPassword = password.getText().toString();
            String userEmail = email.getText().toString();

            Amplify.Auth.signUp(
                    userUsername,
                    userPassword,
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), userEmail).build(),
                result -> {
                        Log.i("Amplify.login", "Result " + result.toString());
                        startActivity(new Intent(Signup.this, SignupConfirmation.class));
                },
                error -> Log.e("Amplify.login", "Error " + error)
        );
        });
    }
}