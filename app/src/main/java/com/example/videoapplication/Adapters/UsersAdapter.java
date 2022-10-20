package com.example.videoapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.videoapplication.Listeners.UsersListener;
import com.example.videoapplication.Models.User;
import com.example.videoapplication.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private List<User> users;
    private UsersListener usersListener;

    public UsersAdapter(List<User> users, UsersListener usersListener){
        this.users = users;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_user,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

    }


    @Override
    public int getItemCount() {
        return users.size();
    }

     class UserViewHolder extends RecyclerView.ViewHolder{

        TextView txtUsername, txtFirstChar,textEmail;
        ImageView audioMeet,videoMeet;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFirstChar= itemView.findViewById(R.id.textfirst);
            txtUsername = itemView.findViewById(R.id.usernameTV);
            textEmail = itemView.findViewById(R.id.textEmail);

            audioMeet = itemView.findViewById(R.id.callMeet);
            videoMeet = itemView.findViewById(R.id.videoMeet);


        }
        void setUserData(User user){
            txtFirstChar.setText(user.firstname.substring(0,1));
            txtUsername.setText(String.format("%s %s",user.firstname,user.lastname));
            textEmail.setText(user.email);


            audioMeet.setOnClickListener(v -> usersListener.initiateAudioMeeting(user));

            videoMeet.setOnClickListener(v -> usersListener.initiateVideoMeeting(user));



        }


    }
}
