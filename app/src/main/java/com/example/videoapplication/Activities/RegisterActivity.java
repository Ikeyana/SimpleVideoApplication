package com.example.videoapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapplication.R;
import com.example.videoapplication.Utilities.Constants;
import com.example.videoapplication.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email,pass,first_name,confpass,last_name;
    Button register;
    TextView loginback;
    ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.emaillET);
        pass = findViewById(R.id.passwordlET);
        first_name = findViewById(R.id.namelET);
        last_name = findViewById(R.id.lastname);
        confpass = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.registerBtn);
        loginback = findViewById(R.id.have_Acc);
        progressBar = findViewById( R.id.registerProgress);


        preferenceManager = new PreferenceManager(getApplicationContext());

        loginback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Enter First Name",Toast.LENGTH_SHORT).show();
                }else  if(last_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Enter Last Name",Toast.LENGTH_SHORT).show();
                }else if(email.getText().toString().trim().isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Enter Email Address",Toast.LENGTH_SHORT).show();
                }else  if(pass.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    Toast.makeText(RegisterActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                }else  if(!confpass.getText().toString().trim().equals(pass.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"Password Don't Match",Toast.LENGTH_SHORT).show();
                }else{
                    signup();
                }

            }
        });
    }

    private void signup() {
        register.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE) ;

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.Key_First_Name, first_name.getText().toString());
        user.put(Constants.Key_Last_Name, last_name.getText().toString());
        user.put(Constants.Key_Email, email.getText().toString());
        user.put(Constants.Key_Password, pass.getText().toString());


        database.collection(Constants.Key_Collection_Users)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.Key_Is_Signed_In,true);
                    preferenceManager.putString(Constants.Key_First_Name,first_name.getText().toString());
                    preferenceManager.putString(Constants.Key_Last_Name,last_name.getText().toString());
                    preferenceManager.putString(Constants.Key_Email,email.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.VISIBLE) ;
                        register.setVisibility(View.VISIBLE);
                        Toast.makeText(RegisterActivity.this,"Registration Failed", Toast.LENGTH_SHORT).show();

                    }
        });

    }
}