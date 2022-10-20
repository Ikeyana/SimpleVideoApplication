package com.example.videoapplication.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.videoapplication.Network.APIClient;
import com.example.videoapplication.Network.APIService;
import com.example.videoapplication.R;
import com.example.videoapplication.Utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IncomingCallActivity extends AppCompatActivity {

    ImageView imageMeet, rejectCall, acceptCall;
    TextView firstChar, userEmail, useName;
    String firstname;
    public String meetingType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        imageMeet = findViewById(R.id.imageMeetType);
        firstChar = findViewById(R.id.txtFirstChar);
        useName = findViewById(R.id.txtUsername);
        userEmail = findViewById(R.id.txtUserEmail);
        rejectCall = findViewById(R.id.imageRejectInvitation);
        acceptCall = findViewById(R.id.imageAcceptInvitation);
        meetingType = getIntent().getStringExtra(Constants.Remote_Message_Meeting_Type);
        firstname = getIntent().getStringExtra(Constants.Key_First_Name);


        useName.setText(String.format("%s %s", firstname, getIntent().getStringExtra(Constants.Key_Last_Name)));
        userEmail.setText(getIntent().getStringExtra(Constants.Key_Email));

        if (firstname != null) {
            firstChar.setText(firstname.substring(0, 1));
        }

        if (meetingType != null) {

            if (meetingType.equals("video")) {
                imageMeet.setImageResource(R.drawable.ic_video);
            } else if (meetingType.equals("audio")) {
                imageMeet.setImageResource(R.drawable.ic_call);
            }

        }

        acceptCall.setOnClickListener(v -> sendInvitationResponse(
                Constants.Remote_Message_Invitation_Accepted,
                getIntent().getStringExtra(Constants.Remote_Message_Inviter_Token)
        ));

        rejectCall.setOnClickListener(v -> sendInvitationResponse(
                Constants.Remote_Message_Invitation_Rejected,
                getIntent().getStringExtra(Constants.Remote_Message_Inviter_Token)
        ));


    }


    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.Remote_Message_Type, Constants.Remote_Message_Invitation);
            data.put(Constants.Remote_Message_Invitation_Response, type);

            body.put(Constants.Remote_Message_Data, data);
            body.put(Constants.Remote_Message_Registration_IDS, tokens);

            sendRemoteMesssage(body.toString(), type);

        } catch (Exception e) {
            Toast.makeText(IncomingCallActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMesssage(String remoteMessageBody, String type) {
        APIClient.getClient().create(APIService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.Remote_Message_Invitation_Accepted)) {

                        try {

                            URL serverURL = new URL("https://meet.jit.si");

                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setRoom(getIntent().getStringExtra(Constants.Remote_Message_Meeting_Room));

                            if (meetingType.equals("audio")) {
                                builder.setVideoMuted(true);
                            }

                            JitsiMeetActivity.launch(IncomingCallActivity.this, builder.build());
                            finish();


                        } catch (Exception e) {
                            Toast.makeText(IncomingCallActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(IncomingCallActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(IncomingCallActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(IncomingCallActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }


    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.Remote_Message_Invitation_Response);
            if (type != null) {
                if (type.equals(Constants.Remote_Message_Invitation_Cancelled)) {
                    Toast.makeText(context, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
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