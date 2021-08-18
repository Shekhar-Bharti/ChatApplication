package com.example.chatapplication.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapplication.Fragments.FriendsFragment;
import com.example.chatapplication.Fragments.GroupsFragment;
import com.example.chatapplication.Models.Users;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Users myProfile;
    public ViewPagerAdapter(@NonNull FragmentManager fm,Users myProfile)
    {
        super(fm);
        this.myProfile=myProfile;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new GroupsFragment(myProfile);
            default:
                return new FriendsFragment(myProfile);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0: return "FRIENDS";
            case 1: return "GROUPS";
        }
        return null;
    }
}
