package com.example.chatapplication.AddFriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapplication.Adapters.RecyclerViewChatsAdapter;
import com.example.chatapplication.Adapters.RecyclerViewUsersAdapter;
import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.security.AccessController.getContext;

public class SendRequest extends AppCompatActivity {

    Users myProfile;
    FirebaseFirestore database;
    ArrayList<Users> allUsersArrayList, searchedList;
    final String TAG= "Send Request";

    EditText editTextSearchId;
    RecyclerView recyclerViewSendRequest;
    Pattern patternFirstName, patternSurName, patternEmail;
    Intent intent;
    RecyclerViewFriendSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextSearchId=findViewById(R.id.editTextSearchId);
        allUsersArrayList=  new ArrayList<>();
        intent= getIntent();
        myProfile=(Users) intent.getSerializableExtra("myProfile");
        editTextSearchId.setText("");

        database =FirebaseFirestore.getInstance();

        searchedList= new ArrayList<>();

        getDetails();

        editTextSearchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
                onSearch(editTextSearchId.getText().toString().trim());
            }
        });

        recyclerViewSendRequest = findViewById(R.id.recyclerViewSendRequest);
        adapter = new RecyclerViewFriendSearchAdapter(searchedList, myProfile,database, this);
        recyclerViewSendRequest.setAdapter(adapter);
        recyclerViewSendRequest.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getDetails(){
        Query query= database.collection("Users").whereNotEqualTo(FieldPath.documentId(),myProfile.getUserId());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    allUsersArrayList.add(document.toObject(Users.class));
                }
            }
            else{
                Log.w(TAG,"failed in getting collection Details");
            }
        });
    }

    private void onSearch(String pattern){
        searchedList.clear();
        if(pattern!=null&&pattern.length()!=0) {
            patternFirstName = Pattern.compile(pattern + ".*");
            patternSurName = Pattern.compile(".* " + pattern);
            patternEmail = Pattern.compile(".*" + pattern + ".*@.*");
            for (Users user : allUsersArrayList) {
                if (patternFirstName.matcher(user.getUserName()).matches()
                        || patternSurName.matcher(user.getUserName()).matches()
                        || patternEmail.matcher(user.getEmailId()).matches()) {
                    searchedList.add(user);
                }
            }
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",true) .addOnSuccessListener(new OnSuccessListener<Void>() {
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
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",false) .addOnSuccessListener(new OnSuccessListener<Void>() {
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