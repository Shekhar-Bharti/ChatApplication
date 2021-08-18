package com.example.chatapplication.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.chatapplication.Adapters.RecyclerViewUsersAdapter;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {


ArrayList<Users> allFriendsTillNow;
Users myProfile;
FirebaseFirestore database;

    public FriendsFragment() {
    }

    public FriendsFragment(Users myProfile) {
        this.myProfile=myProfile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database= FirebaseFirestore.getInstance();
        allFriendsTillNow=new ArrayList<>();
        View view=inflater.inflate(R.layout.fragment_friends, container, false);


        RecyclerViewUsersAdapter recyclerViewUsersAdapter= new RecyclerViewUsersAdapter(allFriendsTillNow, myProfile,(Activity) getContext());
        RecyclerView recyclerView = view.findViewById(R.id.friendsRecyclerView);
        recyclerView.setAdapter(recyclerViewUsersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

       if(myProfile.getAllFriendsTillNow()!=null && myProfile.getAllFriendsTillNow().size()!=0) {
           database.collection("Users").whereIn("userId", myProfile.getAllFriendsTillNow()).addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   for (QueryDocumentSnapshot document : value) {
                       allFriendsTillNow.add(document.toObject(Users.class));
                   }
                   recyclerViewUsersAdapter.notifyDataSetChanged();
                   Log.d("task2", allFriendsTillNow.size() + "");
               }
           });
       }
        return view;
    }
}