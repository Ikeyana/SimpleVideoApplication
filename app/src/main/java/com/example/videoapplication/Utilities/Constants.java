package com.example.videoapplication.Utilities;

import java.util.HashMap;

public class Constants {
    
    public static final String Key_Collection_Users = "users";
    public static final String Key_First_Name = "first_name";
    public static final String Key_Last_Name = "last_name";
    public static final String Key_Email = "email";
    public static final String Key_Password = "password";
    public static final String Key_User_Id = "user_id";
    public static final String Key_FCM_Token =  "fcm_token";

    public static final String Key_Preference_Name = "videoMeetingPreference";
    public static final String Key_Is_Signed_In = "isSignedIn";


    public static final String Remote_Message_Authorization ="Authorization";
    public static final String Remote_Message_Content_type ="Content-Type";


    public static final String Remote_Message_Type = "type";
    public static final String Remote_Message_Invitation = "invitation";
    public static final String Remote_Message_Meeting_Type="meetingType";
    public static final String Remote_Message_Inviter_Token = "inviterToken";
    public static final String Remote_Message_Data = "data";
    public static final String Remote_Message_Registration_IDS = "registration_ids";


    public static final String Remote_Message_Invitation_Response ="invitationResponse";
    public static final String Remote_Message_Invitation_Accepted = "accepted";
    public static final String Remote_Message_Invitation_Rejected = "rejected";
    public static final String Remote_Message_Invitation_Cancelled = "cancelled";


    public static final String Remote_Message_Meeting_Room = "meetingRoom";


    public static HashMap<String, String>getRemoteMessageHeaders(){
        HashMap<String,String> headers = new HashMap<>();

        headers.put(
          Constants.Remote_Message_Authorization,
          "key=AAAAO9HxrTc:APA91bF8YUfMBxEffWAQF_7DZyOWCXDF_UESqBfE9JDzqguMHRXa6rzbWll9OZzZ5tPmijiIivp4Vw6opoEUOa-_gEbwUuVzbzl9Ur7iKn0p42oG3aO9ykuvil2Z1QX1LstzEFUvUJ6e"
        );

        headers.put(Constants.Remote_Message_Content_type,"application/json");
                return headers;

    }
}
