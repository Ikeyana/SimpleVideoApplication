package com.example.videoapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.videoapplication.Adapters.UsersAdapter;
import com.example.videoapplication.Listeners.UsersListener;
import com.example.videoapplication.Models.User;
import com.example.videoapplication.R;
import com.example.videoapplication.Utilities.Constants;
import com.example.videoapplication.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListener {


    TextView title, signout, errorMess;

    private PreferenceManager preferenceManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<User> users;
    private UsersAdapter usersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById( R.id.title);
        signout = findViewById(R.id.signout);
        errorMess = findViewById(R.id.texterror);
        swipeRefreshLayout = findViewById(R.id.swiper);

        preferenceManager = new PreferenceManager(getApplicationContext());

        String wew = preferenceManager.getString(Constants.Key_First_Name );
        String aws = preferenceManager.getString(Constants.Key_Last_Name );

        title.setText(String.format("%s %s", wew,aws));

        swipeRefreshLayout.setOnRefreshListener(this::getUsers);




        signout.setOnClickListener(v -> signOut());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful()&& task.getResult() != null){
                sendFCMToken(task.getResult().getToken());
            }
        });

        RecyclerView userRecyclerView = findViewById(R.id.userRecylerview);

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users,this);
        userRecyclerView.setAdapter(usersAdapter);


        getUsers();
    }

    private void getUsers(){

        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_Collection_Users)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String myUserId = preferenceManager.getString(Constants.Key_User_Id);
                    if(task.isSuccessful() && task.getResult() != null){
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){

                            if(myUserId.equals(documentSnapshot.getId())){
                                continue;
                            }

                            User user = new User();
                            user.firstname = documentSnapshot.getString(Constants.Key_First_Name);
                            user.lastname = documentSnapshot.getString(Constants.Key_Last_Name);
                            user.email = documentSnapshot.getString(Constants.Key_Email);
                            user.token = documentSnapshot.getString(Constants.Key_FCM_Token);
                            users.add(user);
                        }

                        if(users.size()>0){
                            usersAdapter.notifyDataSetChanged();

                        }else{
                            errorMess.setText(String.format("%s","No Users Available"));
                            errorMess.setVisibility(View.VISIBLE);
                        }

                    }else{
                        errorMess.setText(String.format("%s","No Users Available"));
                        errorMess.setVisibility(View.VISIBLE);
                    }
                });
    }



    private void sendFCMToken(String token){
        FirebaseFirestore database =  FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(Constants.Key_Collection_Users).document(
                        preferenceManager.getString(Constants.Key_User_Id)
        );
        documentReference.update(Constants.Key_FCM_Token, token)
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Unable to send token.", Toast.LENGTH_SHORT).show());
    }


    private void signOut(){
        Toast.makeText(MainActivity.this,"Signing out......",Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.Key_Collection_Users).document(
                        preferenceManager.getString(Constants.Key_User_Id)
                );

        HashMap<String, Object> updates = new HashMap<>();

        updates.put(Constants.Key_FCM_Token, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clearPreferences();
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this,"Sign Out Error.",Toast.LENGTH_SHORT).show();

                    }
                });
        }

    @Override
    public void initiateVideoMeeting(User user) {
        if(user.token == null || user.token.trim().isEmpty()){

            Toast.makeText(this,user.firstname+" "+user.lastname +" is not available for meeting",Toast.LENGTH_SHORT).show();

        }else {
            Intent intent = new Intent(getApplicationContext(),OutGoingInvitationActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("type","video");
            startActivity(intent);

        }

    }

    @Override
    public void initiateAudioMeeting(User user) {
        if(user.token == null || user.token.trim().isEmpty()){

            Toast.makeText(this,user.firstname+" "+user.lastname +" is not available for meeting",Toast.LENGTH_SHORT).show();

        }else {
            Intent intent = new Intent(getApplicationContext(),OutGoingInvitationActivity.class);
            intent.putExtra("user",user);
            intent.putExtra("type","audio");
            startActivity(intent);

        }

    }


}