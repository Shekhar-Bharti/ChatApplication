package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatapplication.Adapters.RecyclerViewGroupChatsAdapter;
import com.example.chatapplication.Models.Groups;
import com.example.chatapplication.Models.MessageModel;
import com.example.chatapplication.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatDetailActivity extends AppCompatActivity {

    Groups group;
    Users myProfile;
    TextView groupName;
    EditText messageView;
    ImageView groupPic,backButton,sendButton;
    RecyclerView recyclerView;
    FirebaseFirestore database;
    ArrayList<MessageModel> messageModels;
    ArrayList<Users> groupMembers;
    RecyclerViewGroupChatsAdapter recyclerViewGroupChatsAdapter;
    String TAG="task000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_detail);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        setData(intent);
        initialiseViews();
        database=FirebaseFirestore.getInstance();
        groupName.setText(group.getGroupName());
        groupPic.setImageResource(R.drawable.avatar);
        messageModels =  new ArrayList<>();
        groupMembers= new ArrayList<>();
        fetchGroupMemberData();

    }

    private void fetchGroupMemberData() {
        database.collection("Users").whereIn(FieldPath.documentId(),group.getMembers()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                    {
                        groupMembers.add(queryDocumentSnapshot.toObject(Users.class));
                        fetchMessages();
                    }

                }
                else
                    Log.d("task88","fetch failed in group member detail");
            }


        });
    }
  public void fetchMessages()
  {
      recyclerViewGroupChatsAdapter= new RecyclerViewGroupChatsAdapter(messageModels,myProfile,groupMembers,this);
      recyclerView.setAdapter(recyclerViewGroupChatsAdapter);
      LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(this);
      recyclerView.setLayoutManager(linearLayoutManager);
      database.collection("GroupChatRooms").document(group.getGroupId()).collection("messages").orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
              if(error!=null)
              {
                  Log.d("task452","error find in fetching group messages");return;
              }
              messageModels.clear();
              for(QueryDocumentSnapshot queryDocumentSnapshot:value)
              {
                  messageModels.add(queryDocumentSnapshot.toObject(MessageModel.class));
              }
              recyclerViewGroupChatsAdapter.notifyDataSetChanged();
              recyclerView.scrollToPosition(messageModels.size()-1);
          }
      }
      );
  }

    public void setData(Intent intent)
    {
        myProfile= (Users) intent.getSerializableExtra("myProfile");
        group = (Groups) intent.getSerializableExtra("group");
    }

    public void initialiseViews() {
        recyclerView = findViewById(R.id.chatRecyclerViewOfGroup);
        groupName= findViewById(R.id.groupName);
        groupPic = findViewById(R.id.groupPic);
        messageView = findViewById(R.id.editMessageOfGroup);
        sendButton = findViewById(R.id.sendButtonOfGroup);
        backButton = findViewById(R.id.backButtonOfGroup);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("task77","chaeck1");
              String message = messageView.getText().toString();
              messageView.setText("");
               MessageModel messageModel = new MessageModel(myProfile.getUserId(),message, new Date().getTime());
                database.collection("GroupChatRooms").document(group.getGroupId()).collection("messages").add(messageModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("task100","document added successfully in group");
                    }
                }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("task66","error in adding document in group");
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatDetailActivity.this,UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",true) .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
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
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document on pause", e);
                    }
                });

    }

}