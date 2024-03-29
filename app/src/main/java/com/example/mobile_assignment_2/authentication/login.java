package com.example.mobile_assignment_2.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobile_assignment_2.MainActivity;
import com.example.mobile_assignment_2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


/**
 * @author: Yicong Li
 * @description: when user input his verified email and password, it will be allowed to log in.
 */
public class login extends AppCompatActivity implements View.OnClickListener {



    //fields
    EditText email;
    EditText password;
    Button loginBtn;
    TextView registerInLogin;
    FirebaseAuth myAuth;
    //String defaultUsername = "leo727268082@gmail.com";
    //String defaultUsername = "727268082@qq.com";
    //String defaultUsername = "695578606@qq.com";
    //String defaultPassword = "123456789";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //xml id
        email = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        registerInLogin = findViewById(R.id.registerInLogin);
        myAuth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(this);
        registerInLogin.setOnClickListener(this);
    }


    //from login to register page
    public void loginIntent() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    //from login to home page
    public void mainIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        return;
    }


    //login
    private void loginUser() {

        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();


        if (TextUtils.isEmpty(emailString)) {

            email.setError("Email cannot be empty");
            email.requestFocus();
        } else if (TextUtils.isEmpty(passwordString)) {

            password.setError("Password cannot be empty");
            password.requestFocus();
        } else {

            myAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        FirebaseUser user = myAuth.getCurrentUser();
                        if (user.isEmailVerified()) {
                            Snackbar.make(loginBtn, "User login successfully", Snackbar.LENGTH_SHORT).show();
                            mainIntent();
                            finish();
                        } else {
                            Snackbar.make(loginBtn, "Email address has not been verified", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(loginBtn, "Login error occurs: " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    //page switcher
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loginButton:
                loginUser();
                break;
            case R.id.registerInLogin:
                loginIntent();
                break;
        }

    }
}