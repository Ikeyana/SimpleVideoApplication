package com.example.videoapplication.firebase;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.videoapplication.Activities.IncomingCallActivity;
import com.example.videoapplication.Utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constants.Remote_Message_Type);

        if(type != null ){
            if(type.equals(Constants.Remote_Message_Invitation)){
                Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
                intent.putExtra(
                        Constants.Remote_Message_Meeting_Type,
                        remoteMessage.getData().get(Constants.Remote_Message_Meeting_Type)
                );
                intent.putExtra(
                        Constants.Key_First_Name,
                        remoteMessage.getData().get(Constants.Key_First_Name)
                );
                intent.putExtra(
                        Constants.Key_Last_Name,
                        remoteMessage.getData().get(Constants.Key_Last_Name)
                );
                intent.putExtra(
                        Constants.Key_Email,
                        remoteMessage.getData().get(Constants.Key_Email)
                );
                intent.putExtra(
                        Constants.Remote_Message_Inviter_Token,
                        remoteMessage.getData().get(Constants.Remote_Message_Inviter_Token)
                );
                intent.putExtra(
                        Constants.Remote_Message_Inviter_Token,
                        remoteMessage.getData().get(Constants.Remote_Message_Inviter_Token)
                );
                intent.putExtra(
                        Constants.Remote_Message_Meeting_Room,
                        remoteMessage.getData().get(Constants.Remote_Message_Meeting_Room)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }

            if(type.equals(Constants.Remote_Message_Invitation_Response)){
                Intent intent = new Intent(Constants.Remote_Message_Invitation_Response);
                intent.putExtra(
                        Constants.Remote_Message_Invitation_Response,
                        remoteMessage.getData().get(Constants.Remote_Message_Invitation_Response)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }


        }

    }
}
