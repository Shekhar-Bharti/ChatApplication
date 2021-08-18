package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Adapters.RecyclerViewChatsAdapter;
import com.example.chatapplication.Models.MessageModel;
import com.example.chatapplication.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatDetailActivity extends AppCompatActivity {

    TextView friendName,isOnlineView;
    EditText messageView;
    ImageView friendPic,sendButton;
    Users friend,myProfile;
    RecyclerView recyclerView;
    String chatId;
    FirebaseFirestore database;
    ArrayList<MessageModel> messageModels ;
    String TAG = "task99";
    Boolean isFriendOnline=false,isFriendTyping=false;
    Boolean myTypingStatus=false;
    RecyclerViewChatsAdapter recyclerViewChatsAdapter;
    HashMap<String, Integer> mapOfIndexesWithMessageId;
    MessageModel message;
    int index;
    ListenerRegistration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        setData(intent);
        generateChatId();
        initialiseViews();
        database=FirebaseFirestore.getInstance();
        friendName.setText(friend.getUserName());
        friendPic.setImageResource(R.drawable.avatar);
        messageModels =  new ArrayList<>();
        mapOfIndexesWithMessageId=new HashMap<>();
        recyclerViewChatsAdapter= new RecyclerViewChatsAdapter(messageModels,myProfile,ChatDetailActivity.this);
        recyclerView.setAdapter(recyclerViewChatsAdapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        attachIsFriendOnlineListener();
        attachIsFriendTypingListener();
        attachMyTypingListener();



    }
    public void updateMessageSeen(String messageId)
    {
        database.collection("ChatRooms").document(chatId).collection("messages").document(messageId).update("seen",true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("task1","Message updated to seen");
            }
        });
    }

        public  void attachIsFriendOnlineListener()
        {
            database.collection("Status").document(friend.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.getBoolean("isOnline"))
                        isFriendOnline=true;
                    else
                        isFriendOnline=false;
                updateFriendStatus();
                }
            });
        }
      public void attachIsFriendTypingListener()
      {
          database.collection("ChatRoomsTypingStatus").document(chatId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
              @Override
              public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                  if(value.getBoolean(friend.getUserId()+"isTyping"))
                      isFriendTyping=true;
                  else
                      isFriendTyping=false;
                  updateFriendStatus();
              }
          });
      }
      public  void updateFriendStatus()
      {
          if(!isFriendOnline)
              isOnlineView.setVisibility(View.INVISIBLE);
          else if(isFriendTyping) {
              isOnlineView.setVisibility(View.VISIBLE);
              isOnlineView.setText("Typing..");
          }
          else
          {
              isOnlineView.setVisibility(View.VISIBLE);
              isOnlineView.setText("Online");
          }


      }
  public void updateMyTypingStatusToTrue()
  {
      database.collection("ChatRoomsTypingStatus").document(chatId).update(myProfile.getUserId()+"isTyping",true).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              Log.d("task99","MyTypingStatusUpdatedToTrue");
          }
      });
  }
    public void updateMyTypingStatusToFalse()
    {
        database.collection("ChatRoomsTypingStatus").document(chatId).update(myProfile.getUserId()+"isTyping",false).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("task99","MyTypingStatusUpdatedToFalse");
            }
        });
    }
    public void attachMyTypingListener()
    {
        messageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if(messageView.getText().toString().length()==0)
                     {
                         myTypingStatus=false;
                         updateMyTypingStatusToFalse();
                     }
                     else if(myTypingStatus==false) {
                         myTypingStatus=true;
                         updateMyTypingStatusToTrue();
                     }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void setData(Intent intent)
    {
        friend=(Users)intent.getSerializableExtra("friend");
        myProfile=(Users)intent.getSerializableExtra("myProfile");
    }
    public void generateChatId()
    {
        if(friend.getUserId().compareTo(myProfile.getUserId())<0)
        {
          chatId=friend.getUserId()+myProfile.getUserId();
        }
        else
            chatId=myProfile.getUserId()+friend.getUserId();
    }
    public void initialiseViews()
    {
        isOnlineView = findViewById(R.id.isOnline);
        recyclerView =findViewById(R.id.chatRecyclerView);
        friendName = findViewById(R.id.userName1);
        friendPic = findViewById(R.id.profilePic);
        messageView=findViewById(R.id.editMessage);
        sendButton=findViewById(R.id.sendButton);
//        Toast.makeText(this,friend.getEmailId(),Toast.LENGTH_LONG).show();
        if(!myProfile.getFriends().contains(friend.getUserId())){
            sendButton.setVisibility(View.INVISIBLE);
            messageView.setText("Not Your Friend");
            messageView.setEnabled(false);
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String message=  messageView.getText().toString();
                MessageModel messageModel =new MessageModel(myProfile.getUserId(),message,false, new Date().getTime());
                messageView.setText("");
                Log.d("task4","going to connect");
               database.collection("ChatRooms").document(chatId).collection("messages").add(messageModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("task10","document added successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("task11","error in adding document");
                    }
                });

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        messageModels.clear();
            database.collection("Status").document(myProfile.getUserId()).update("isOnline", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        registration=database.collection("ChatRooms").document(chatId).collection("messages").orderBy("timeStamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.d("task12","Listen failed");
                    return;
                }

                for(DocumentChange dc: value.getDocumentChanges())
                {
                    if(dc.getDocument().exists()) {
                        message = dc.getDocument().toObject(MessageModel.class);
                        switch (dc.getType())
                        {
                            case ADDED:
                                messageModels.add(message);
                                mapOfIndexesWithMessageId.put(dc.getDocument().getId(),messageModels.size()-1);
                                Log.d("task7","document added");
                                if(!message.getSenderId().equals(myProfile.getUserId()))
                                {
                                    updateMessageSeen(dc.getDocument().getId());
                                }
                                recyclerViewChatsAdapter.notifyItemInserted(messageModels.size()-1);
                                recyclerView.scrollToPosition(messageModels.size()-1);
                                break;
                            case MODIFIED:
                                index=mapOfIndexesWithMessageId.get(dc.getDocument().getId());
                                messageModels.set(index,message);
                                Log.d("task7","document changed");
                                recyclerViewChatsAdapter.notifyItemChanged(index);
                                break;
                        }

                    }
                }
                recyclerViewChatsAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageModels.size()-1);
            }
        });




    }

    @Override
    protected void onPause() {
        super.onPause();
         registration.remove();
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