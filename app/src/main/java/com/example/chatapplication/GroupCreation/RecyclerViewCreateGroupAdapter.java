package com.example.chatapplication.GroupCreation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.chatapplication.AddFriend.RecyclerViewFriendSearchAdapter;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

public class RecyclerViewCreateGroupAdapter extends RecyclerView.Adapter<RecyclerViewCreateGroupAdapter.ViewHolder> {
    ArrayList<Users> searchedResult;
    Users currentUser;
    Context context;
    ArrayList<Users> added;
    public static final int ADDED=0, NOT_ADDED=1;
    UpdateFromAdapterMain updateFromAdapter;

    public RecyclerViewCreateGroupAdapter(ArrayList<Users> searchedResult,ArrayList<Users> added, Users currentUser, UpdateFromAdapterMain updateFromAdapter, Context context) {
        this.searchedResult = searchedResult;
        this.currentUser = currentUser;
        this.updateFromAdapter=updateFromAdapter;
        this.context = context;
        this.added=added;
    }

    @NonNull
    @Override
    public RecyclerViewCreateGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sample_create_group,parent,false);
        return new RecyclerViewCreateGroupAdapter.ViewHolder(view,updateFromAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewCreateGroupAdapter.ViewHolder holder, int position) {
        Users user=searchedResult.get(position);

        holder.email.setText(user.getEmailId());
        holder.name.setText(user.getUserName());
        holder.state= getCurrentState(user);
        setIcons(holder);
        setBackGround(holder);

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(user.getDpMidLocation());
        try{
            setProfilePic(storageReference,holder);
        }catch(Exception e){
            holder.profilePic.setImageResource(R.drawable.avatar);
        }

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInGroup(user,holder);
            }
        });
    }
    private void addInGroup(Users user,ViewHolder holder){
        try {
            switch(holder.state){
                case ADDED :
                    holder.state= NOT_ADDED;
                    holder.updateFromAdapter.discardMemberMain(user);
                    break;

                default:
                    holder.state=ADDED;
                    holder.updateFromAdapter.addMember(user);
                    break;
            }}catch (Exception e){
            Log.d("CreateGroupAdapter","Error: "+e.getMessage());
        }
        setIcons(holder);
        setBackGround(holder);
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

    private void setIcons(ViewHolder holder){
        if(holder.state==ADDED){
            holder.addButton.setImageResource(R.drawable.ic_minus);
        }else{
            holder.addButton.setImageResource(R.drawable.icon_plus);
        }
    }

    private void setBackGround(ViewHolder holder) {
        if(holder.state==ADDED){
            holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.cream_yellow));
        }else{
            holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    Integer getCurrentState(Users user){
        if(added.contains(user)){
            return ADDED;
        }
        return NOT_ADDED;
    }

    @Override
    public int getItemCount() {
        return searchedResult.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, email;
        ImageView profilePic, addButton;
        Integer state;
        UpdateFromAdapterMain updateFromAdapter;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView, UpdateFromAdapterMain updateFromAdapter) {
            super(itemView);
            this.updateFromAdapter= updateFromAdapter;
            constraintLayout=itemView.findViewById(R.id.constraintLayoutCreateGroupHolder);
            email = itemView.findViewById(R.id.textViewEmailCreateGroup);
            name = itemView.findViewById(R.id.textViewUserNameCreateGroup);
            profilePic = itemView.findViewById(R.id.profilePicOfUserCreateGroup);
            addButton= itemView.findViewById(R.id.imageButtonAddCreateGroup);
        }
    }

    interface UpdateFromAdapterMain{
        void addMember(Users user);
        void discardMemberMain(Users user);
    }
}
