package com.example.chatapplication.LoginSignUp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {
    private Context context;
    int totalTabs;

    public LoginAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem(int position){
        switch (position){
            case 0: FragmentLoginTab fragmentLoginTab= new FragmentLoginTab();
                return fragmentLoginTab;
            case 1: FragmentSignUpTab fragmentSignUpTab= new FragmentSignUpTab();
                return fragmentSignUpTab;
            default: return null;
        }
    }
}