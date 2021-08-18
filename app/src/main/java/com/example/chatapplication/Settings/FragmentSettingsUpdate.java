package com.example.chatapplication.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;

public class FragmentSettingsUpdate extends Fragment {
    Users currentUser;

    TextView textViewUpdateUserName, textViewUpdateStatus, textViewUpdateBio, textViewUpdateEmail;

    public FragmentSettingsUpdate(Users currentUser){
        //constructor;
        this.currentUser=currentUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_update,container,false);

        textViewUpdateBio=view.findViewById(R.id.textViewUpdateBio);
        textViewUpdateEmail=view.findViewById(R.id.textViewUpdateEmail);
        textViewUpdateStatus=view.findViewById(R.id.textViewUpdateStatus);
        textViewUpdateUserName=view.findViewById(R.id.textViewUpdateUserName);

        textViewUpdateUserName.setText(currentUser.getUserName());
        textViewUpdateEmail.setText(currentUser.getEmailId());
        textViewUpdateBio.setText(currentUser.getBio());

        //textViewUpdateStatus.setText(currentUser.getStatus());


        return view;
    }
}
