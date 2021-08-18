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

import com.example.chatapplication.Adapters.RecyclerViewGroupsAdapter;
import com.example.chatapplication.Adapters.RecyclerViewUsersAdapter;
import com.example.chatapplication.Models.Groups;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class GroupsFragment extends Fragment {


    ArrayList<Groups>  groups;
    Groups group;
    Users myProfile;
    FirebaseFirestore database;

    public GroupsFragment() {
    }

    public GroupsFragment(Users myProfile) {
        // Required empty public constructor
        this.myProfile=myProfile;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        database= FirebaseFirestore.getInstance();
        groups= new ArrayList<>();
        View view= inflater.inflate(R.layout.fragment_groups, container, false);
        RecyclerViewGroupsAdapter recyclerViewGroupsAdapter = new RecyclerViewGroupsAdapter(groups, myProfile, (Activity) getContext());
        RecyclerView recyclerView = view.findViewById(R.id.groupsRecyclerView);
        recyclerView.setAdapter(recyclerViewGroupsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if(myProfile.getGroups()!=null && myProfile.getGroups().size()!=0) {
            database.collection("Groups").whereIn(FieldPath.documentId(), myProfile.getGroups()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot document : value) {
                        group = (document.toObject(Groups.class));
                        group.setGroupId(document.getId());
                        groups.add(group);
                    }
                    recyclerViewGroupsAdapter.notifyDataSetChanged();
                    Log.d("task2", groups.size() + "");
                }
            });
        }
        return view;
    }

}