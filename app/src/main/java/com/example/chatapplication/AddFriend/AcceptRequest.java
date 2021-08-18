package com.example.chatapplication.AddFriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AcceptRequest extends AppCompatActivity {

    Users myProfile;
    Intent intent;
    TextView textViewNoRequest;
    RecyclerView recyclerViewAcceptRequest;
    RecyclerViewFriendSearchAdapter adapter;
    ArrayList<Users>  requestedFriends;
    FirebaseFirestore database;
    String TAG="task88";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewNoRequest=findViewById(R.id.textViewAcceptMethodNoRequest);
        textViewNoRequest.setVisibility(View.INVISIBLE);

        intent= getIntent();
        myProfile=(Users) intent.getSerializableExtra("myProfile");

        database =FirebaseFirestore.getInstance();

        requestedFriends=  new ArrayList<>();
        if(myProfile.getFriendRequests()!=null && myProfile.getFriendRequests().size()!=0) {
            textViewNoRequest.setVisibility(View.INVISIBLE);
            getDetails();
        }else{
            textViewNoRequest.setVisibility(View.VISIBLE);
            textViewNoRequest.setText("You Don't have any pending Request");
        }
        recyclerViewAcceptRequest = findViewById(R.id.recyclerViewAcceptRequest);
        adapter = new RecyclerViewFriendSearchAdapter(requestedFriends, myProfile,database, this);
        recyclerViewAcceptRequest.setAdapter(adapter);
        recyclerViewAcceptRequest.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getDetails(){
        Query query= database.collection("Users").whereIn(FieldPath.documentId(),myProfile.getFriendRequests());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    requestedFriends.add(document.toObject(Users.class));
                }
                adapter.notifyDataSetChanged();
            }
            else{
                Log.w(TAG,"failed in getting collection Details");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated on pause!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document on pause", e);
            }
        });

    }

}