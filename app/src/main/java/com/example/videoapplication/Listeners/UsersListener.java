package com.example.videoapplication.Listeners;

import com.example.videoapplication.Models.User;

public interface UsersListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);
}
