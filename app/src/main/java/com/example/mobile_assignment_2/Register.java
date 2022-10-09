package com.example.mobile_assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {


    EditText username;
    EditText password;
    EditText email;
    TextView back;
    Button registerBtn;
    FirebaseAuth myAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = findViewById(R.id.registerUsername);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerBtn = findViewById(R.id.registerButton);
        back = findViewById(R.id.registerBack);

        myAuth = FirebaseAuth.getInstance();


        registerBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                createUser();
                //registerIntent();
            }
        });

        back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                registerIntent();
            }
        });



    }
    //link to login page
    public void registerIntent(){
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }


    //register a user
    private void createUser() {


        String usernameString = username.getText().toString();
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();


        if(TextUtils.isEmpty(usernameString)){

            username.setError("username cannot be empty");
            username.requestFocus();

        }
        else if(TextUtils.isEmpty(emailString)){

            email.setError("Email cannot be empty");
            email.requestFocus();

        }
        else if(TextUtils.isEmpty(passwordString)){

            password.setError("Password cannot be empty");
            password.requestFocus();

        }else{

            myAuth.createUserWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        FirebaseUser user = myAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                registerIntent();
                            }
                        }). addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("OnFailure:Email not sent" + e.getMessage());
                            }
                        });
                    }else{
                        Toast.makeText(Register.this, "failed to create an account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}