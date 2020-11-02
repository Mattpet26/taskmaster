package com.petersen.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;

public class SignupConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_confirmation);

        Button confirmLogin = findViewById(R.id.submitConfirmation);
        confirmLogin.setOnClickListener(view ->{

            EditText username = SignupConfirmation.this.findViewById(R.id.usernameConfirmation);
            EditText confirmCode = SignupConfirmation.this.findViewById(R.id.confirmationCode);

            String userUsername = username.getText().toString();
            String userConfirmCode = confirmCode.getText().toString();

        Amplify.Auth.confirmSignUp(
                userUsername,
                userConfirmCode,
                result -> {
                    Log.i("Amplify.login", "Result " + result.toString());
                    startActivity(new Intent(SignupConfirmation.this, Signin.class));
                },
                error -> Log.e("Amplify.login", "Error " + error)
            );
        });
    }
}