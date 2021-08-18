package com.example.chatapplication.AddFriend;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.R.id;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecyclerViewFriendSearchAdapter extends RecyclerView.Adapter<RecyclerViewFriendSearchAdapter.ViewHolder> {

    ArrayList<Users> searchedResult;
    Users currentUser;
    Context context;
    Set<String> friends, requestGot, requestSend;
    FirebaseFirestore firebaseFirestore;
    public static final int FRIENDS=0, REQUEST_GOT=1, REQUEST_SEND=2, NON_FRIEND=3;
    public static final String Friends="friends", RequestGot="friendRequests", RequestSend="requestSend",AllFriendsTillNow ="allFriendsTillNow";


    public RecyclerViewFriendSearchAdapter(ArrayList<Users> searchedResult, Users currentUser,
                                           FirebaseFirestore firebaseFirestore,Context context) {
        this.searchedResult = searchedResult;
        this.currentUser = currentUser;
        this.context = context;
        friends =new HashSet<>();
        requestGot=new HashSet<>();
        requestSend= new HashSet<>();
        friends.addAll(currentUser.getFriends());
        requestSend.addAll(currentUser.getRequestSend());
        requestGot.addAll(currentUser.getFriendRequests());
        this.firebaseFirestore=firebaseFirestore;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sample_friend_request,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user=searchedResult.get(position);
        holder.email.setText(user.getEmailId());
        holder.name.setText(user.getUserName());
        holder.state= getCurrentState(user);

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(user.getDpMidLocation());
        try{
            setProfilePic(storageReference,holder);
        }catch(Exception e){
            holder.profilePic.setImageResource(R.drawable.avatar);
        }
        setIcons(holder);

        holder.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    action(user, position, holder);
                }catch (Exception e) {
                    Log.d("actionClicked", "Error: " + e.getMessage());
                    Toast.makeText(context,"1st Action button Error: ",Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                actionDelete(user,position,holder);
                }catch (Exception e){
                    Log.d("actionDeleteClicked","Error: "+e.getMessage());
                    Toast.makeText(context,"2nd action button error: ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    int getCurrentState(Users user){
        if(friends.contains(user.getUserId())) {
            return FRIENDS;
        }else if (requestSend.contains(user.getUserId())) {
            return REQUEST_SEND;
        }else if (requestGot.contains(user.getUserId())) {
            return REQUEST_GOT;
        }
        return NON_FRIEND;
    }

    private void setIcons(@NonNull ViewHolder holder){
        try {
            switch(holder.state){
                case FRIENDS :
                    holder.addFriendButton.setImageResource(R.drawable.ic_chat);
                    holder.deleteFriendButton.setImageResource(R.drawable.ic_baseline_person_remove_24);
                    holder.deleteFriendButton.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_GOT :
                    holder.addFriendButton.setImageResource(R.drawable.ic_add);
                    holder.deleteFriendButton.setImageResource(R.drawable.ic_delete);
                    holder.deleteFriendButton.setVisibility(View.VISIBLE);
                    break;

                case REQUEST_SEND :
                    holder.deleteFriendButton.setVisibility(View.INVISIBLE);
                    holder.addFriendButton.setImageResource(R.drawable.ic_cancel);
                    break;

                default:
                    holder.deleteFriendButton.setVisibility(View.INVISIBLE);
                    holder.addFriendButton.setImageResource(R.drawable.ic_friend_add);

            }}catch (Exception e){
            Log.d("SearchAdapter","Error: "+e.getMessage());
        }
    }

    @GlideModule
    public class MyAppGlideModule extends AppGlideModule {

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference.class, InputStream.class,
                    new FirebaseImageLoader.Factory());
        }
    }

    void setProfilePic(StorageReference storageReference, ViewHolder holder) throws Exception{

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .placeholder(R.drawable.avatar)
                        .into(holder.profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.profilePic.setImageResource(R.drawable.avatar);
            }
        });
    }

    private void action(Users userFriend,int position,@NonNull ViewHolder holder) throws Exception{
        DocumentReference documentReferenceFriend= firebaseFirestore.collection("Users").document(userFriend.getUserId());
        DocumentReference documentReferenceUser= firebaseFirestore.collection("Users").document(currentUser.getUserId());

        switch(holder.state){
            case NON_FRIEND:
                holder.state=REQUEST_SEND;
                requestSend.add(userFriend.getUserId());
                documentReferenceUser.update(RequestSend, FieldValue.arrayUnion(userFriend.getUserId()));
                documentReferenceFriend.update(RequestGot,FieldValue.arrayUnion(currentUser.getUserId()));
                //Request Send;
                break;

            case REQUEST_GOT:
                holder.state=FRIENDS;
                requestGot.remove(userFriend.getUserId());
                friends.add(userFriend.getUserId());
                documentReferenceFriend.update(RequestSend,FieldValue.arrayRemove(currentUser.getUserId()));
                documentReferenceUser.update(RequestGot,FieldValue.arrayRemove(userFriend.getUserId()));
                documentReferenceUser.update(Friends, FieldValue.arrayUnion(userFriend.getUserId()));
                documentReferenceFriend.update(Friends,FieldValue.arrayUnion(currentUser.getUserId()));
                //Make Friends;
                documentReferenceUser.update(AllFriendsTillNow,FieldValue.arrayUnion(userFriend.getUserId()));
                documentReferenceFriend.update(AllFriendsTillNow,FieldValue.arrayUnion(currentUser.getUserId()));
                //Make chatRoomTypingStatus
                Map<String,Boolean> map= new HashMap<String,Boolean>();
                map.put(currentUser.getUserId()+"isTyping",false);
                map.put(userFriend.getUserId()+"isTyping",false);
                firebaseFirestore.collection("ChatRoomsTypingStatus").document(generateChatId(currentUser,userFriend)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("task77","ChatRoomTypingStatus Created");
                    }
                });
                break;

            case REQUEST_SEND:
                holder.state=NON_FRIEND;
                requestSend.remove(userFriend.getUserId());
                documentReferenceFriend.update(RequestGot,FieldValue.arrayRemove(currentUser.getUserId()));
                documentReferenceUser.update(RequestSend,FieldValue.arrayRemove(userFriend.getUserId()));
                //Request Cancel;
                break;

            default:
                //send to chat;
        }
        setIcons(holder);
    }

    public String generateChatId(Users user1,Users user2)
    {
        if(user1.getUserId().compareTo(user2.getUserId())<0) {
            return user1.getUserId()+user2.getUserId();
        }
        return user2.getUserId()+user1.getUserId();
    }

    private void actionDelete(Users userFriend,int position,@NonNull ViewHolder holder) throws Exception{
        DocumentReference documentReferenceFriend= firebaseFirestore.collection("Users").document(userFriend.getUserId());
        DocumentReference documentReferenceUser= firebaseFirestore.collection("Users").document(currentUser.getUserId());

        switch(holder.state){
            case REQUEST_GOT:
                holder.state=NON_FRIEND;
                requestGot.remove(userFriend.getUserId());
                documentReferenceFriend.update(RequestSend,FieldValue.arrayRemove(currentUser.getUserId()));
                documentReferenceUser.update(RequestGot,FieldValue.arrayRemove(userFriend.getUserId()));
                //request delete;
                break;

            default:
                holder.state=NON_FRIEND;
                friends.remove(userFriend.getUserId());
                documentReferenceUser.update(Friends, FieldValue.arrayRemove(userFriend.getUserId()));
                documentReferenceFriend.update(Friends,FieldValue.arrayRemove(currentUser.getUserId()));
                //unFriend;
        }
        setIcons(holder);
    }

    @Override
    public int getItemCount() {
        return searchedResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, email, bio;
        ImageView profilePic, addFriendButton, deleteFriendButton;
        Integer state;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            email = itemView.findViewById(R.id.textViewEmailFriend);
            name = itemView.findViewById(R.id.textViewUserNameFriend);
            bio = itemView.findViewById(R.id.textViewBioFriend);
            profilePic = itemView.findViewById(R.id.profilePicOfUserFriend);
            addFriendButton= itemView.findViewById(R.id.imageViewAddFriend);
            deleteFriendButton= itemView.findViewById(id.imageViewDeleteFriend);
        }
    }

}
