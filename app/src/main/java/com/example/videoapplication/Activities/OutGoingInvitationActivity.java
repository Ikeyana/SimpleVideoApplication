package com.example.videoapplication.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.videoapplication.Models.User;
import com.example.videoapplication.Network.APIClient;
import com.example.videoapplication.Network.APIService;
import com.example.videoapplication.R;
import com.example.videoapplication.Utilities.Constants;
import com.example.videoapplication.Utilities.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutGoingInvitationActivity extends AppCompatActivity {


    ImageView imageMeet,stopCall;
    TextView firstChar,userEmail,useName;

    private PreferenceManager preferenceManager;
    private String inviterToken = null;

    private String meetingType = null;
    String meetingRoom= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_going_invitation);

        imageMeet = findViewById(R.id.imageMeetType);
        meetingType = getIntent().getStringExtra("type");
        firstChar = findViewById(R.id.txtFirstChar);
        useName = findViewById(R.id.txtUsername);
        userEmail = findViewById(R.id.txtUserEmail);
        stopCall = findViewById(R.id.imageStopInvitation);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if(meetingType !=null){

            if(meetingType.equals("video")){
                imageMeet.setImageResource(R.drawable.ic_video);
            }else if(meetingType.equals("audio")){
                imageMeet.setImageResource(R.drawable.ic_call);
            }

        }

        User user = (User) getIntent().getSerializableExtra("user");

        if(user !=null){
            firstChar.setText(user.firstname.substring(0,1));
            useName.setText(String.format("%s %s", user.firstname,user.lastname));
            userEmail.setText(user.email);
        }

        stopCall.setOnClickListener(v -> {
            if(user !=null){
                cancelInvitation(user.token);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult()!=null){
                inviterToken = task.getResult().getToken();

                if(meetingType !=null && user != null){
                    intiateMeeting(meetingType,user.token);

                }

            }
        });



    }


    private void intiateMeeting(String meetingType, String receiverToken){
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.Remote_Message_Type, Constants. Remote_Message_Invitation);
            data.put(Constants.Remote_Message_Meeting_Type,meetingType);
            data.put(Constants.Key_First_Name,preferenceManager.getString(Constants.Key_First_Name));
            data.put(Constants.Key_Last_Name,preferenceManager.getString(Constants.Key_Last_Name));
            data.put(Constants.Key_Email,preferenceManager.getString(Constants.Key_Email));
            data.put(Constants.Remote_Message_Inviter_Token,inviterToken);

            meetingRoom = preferenceManager.getString(Constants.Key_User_Id )+"_"+ UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.Remote_Message_Meeting_Room, meetingRoom);


            body.put(Constants.Remote_Message_Data,data);
            body.put(Constants.Remote_Message_Registration_IDS, tokens);

            sendRemoteMesssage(body.toString(), Constants.Remote_Message_Invitation);

        }catch (Exception e){
            Toast.makeText(OutGoingInvitationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendRemoteMesssage(String remoteMessageBody,String type){
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.Remote_Message_Invitation)){
                        Toast.makeText(OutGoingInvitationActivity.this, "Invitation Sent Successfully",Toast.LENGTH_SHORT).show();

                    }else if(type.equals(Constants.Remote_Message_Invitation_Response)){
                        Toast.makeText(OutGoingInvitationActivity.this, "Invitation Cancelled",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }else{
                    Toast.makeText(OutGoingInvitationActivity.this,response.message(),Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutGoingInvitationActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void cancelInvitation(String receiverToken){
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();


            data.put(Constants.Remote_Message_Type, Constants.Remote_Message_Invitation_Response);
            data.put(Constants.Remote_Message_Invitation_Response,Constants.Remote_Message_Invitation_Cancelled);

            body.put(Constants.Remote_Message_Data,data);
            body.put(Constants.Remote_Message_Registration_IDS, tokens);

            sendRemoteMesssage(body.toString(), Constants.Remote_Message_Invitation_Response);


        }catch (Exception e){
            Toast.makeText(OutGoingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.Remote_Message_Invitation_Response);

            if(type!=null){
                if(type.equals(Constants.Remote_Message_Invitation_Accepted)){
                    try{

                        URL serverURL = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);

                        if(meetingType.equals("audio")){
                            builder.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(OutGoingInvitationActivity.this, builder.build());
                        finish();


                    }catch (Exception e){
                        Toast.makeText(context, e.getMessage(),Toast.LENGTH_SHORT).show();
                        finish();
                    }


                }else if(type.equals(Constants.Remote_Message_Invitation_Rejected)){

                    Toast.makeText(context,"Invitation Rejected",Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.Remote_Message_Invitation_Response)
        );

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}