package com.example.optimizer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextView textView;

    EditText editTextEmail, editTextPassword, editTextCngPassword, editTextFullname;
    Button buttonReg;
    FirebaseAuth mAuth;
    private SharedPreferencesHelper preferencesHelper;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferencesHelper = new SharedPreferencesHelper(this);

        textView = (TextView) findViewById(R.id.have_an_acc);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        //Firebase authentication

        editTextEmail = findViewById(R.id.email);
        editTextFullname = findViewById(R.id.fullName);
        editTextPassword = findViewById(R.id.password);
        editTextCngPassword = findViewById(R.id.cng_password);
        buttonReg = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, con_pass;
                con_pass = String.valueOf(editTextCngPassword.getText());
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Password Must be >=6 Character and Numbers Only", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(con_pass)){
                    System.out.println("equals");
                    System.out.println(password+"  "+con_pass);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(Register.this, "Authentication done.", Toast.LENGTH_SHORT).show();
                                        preferencesHelper.setFirstTime(false);
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }else{
                    System.out.println("not Equals");
                    System.out.println(password+"  "+con_pass);
                    Toast.makeText(Register.this, "Password Do not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

    }
}


