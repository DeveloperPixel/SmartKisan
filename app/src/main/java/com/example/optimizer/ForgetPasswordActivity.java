package com.example.optimizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    TextView textView;
    Button forgetBtn;
    EditText textEmail;
    String email;
    FirebaseAuth auth;
    public static final String TAG = "ERROR HANDLING.......!";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_forget_password);

        Log.d(TAG,"start onCreate");
        textView = (TextView) findViewById(R.id.goto_login);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onclick in go to login");
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.email);
        forgetBtn = findViewById(R.id.reset_btn);

        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"forgot btn");
                validateData();
            }
        });

    }

    private void validateData() {
        email = textEmail.getText().toString();
        if (email.isEmpty()) {
            textEmail.setError("Required");
        } else {
            forgetPassword();
        }
    }

    private void forgetPassword() {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"Email Sent");
                    Toast.makeText(ForgetPasswordActivity.this, "Check Your Email.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgetPasswordActivity.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(ForgetPasswordActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}