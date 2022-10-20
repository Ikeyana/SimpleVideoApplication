package com.example.videoapplication.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapplication.R;
import com.example.videoapplication.Utilities.Constants;
import com.example.videoapplication.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    TextView signUp;
    Button loginbtn;
    EditText email,pass;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUp = findViewById(R.id.dont_have);
        loginbtn =findViewById(R.id.login_Butt);
        email = findViewById(R.id.emailmm);
        pass = findViewById(R.id.passwordmm);

        preferenceManager = new PreferenceManager(getApplicationContext());




        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             if(email.getText().toString().trim().isEmpty()){
                Toast.makeText(LoginActivity.this,"Enter Email Address",Toast.LENGTH_SHORT).show();
            }else  if(pass.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            }else{
                 login();
             }

            }
        });

    }

    private void login() {
        loginbtn.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_Collection_Users)
                .whereEqualTo(Constants.Key_Email, email.getText().toString())
                .whereEqualTo(Constants.Key_Password, pass.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() !=null && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.Key_Is_Signed_In,true);
                            preferenceManager.putString(Constants.Key_User_Id, documentSnapshot.getId());
                            preferenceManager.putString(Constants.Key_First_Name, documentSnapshot.getString(Constants.Key_First_Name));
                            preferenceManager.putString(Constants.Key_Last_Name,documentSnapshot.getString(Constants.Key_Last_Name));
                            preferenceManager.putString(Constants.Key_Email,documentSnapshot.getString(Constants.Key_Email));

                           // Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }else{
                            loginbtn.setVisibility(View.VISIBLE);

                            Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }
}