package com.example.chatapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.chatapplication.AddFriend.RecyclerViewFriendSearchAdapter;
import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

public class RecyclerViewUsersAdapter extends RecyclerView.Adapter<RecyclerViewUsersAdapter.ViewHolder>{

 ArrayList<Users> allFriendsTillNow;
 Activity context;
 Users myProfile;

    public RecyclerViewUsersAdapter(ArrayList<Users> allFriendsTillNow, Users myProfile,Activity context) {
        this.allFriendsTillNow = allFriendsTillNow;
        this.context = context;
        this.myProfile=myProfile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.sample_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users friend= allFriendsTillNow.get(position);
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(friend.getDpMidLocation());
        try {
            setProfilePic(storageReference,holder);
        } catch (Exception e) {
            holder.image.setImageResource(R.drawable.avatar);
        }
        holder.userName.setText((String)friend.getUserName());
        holder.lastMessage.setText("lastMessage");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("friend",friend);
                intent.putExtra("myProfile",myProfile);
                context.startActivity(intent);
            }
        });
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
                        .into(holder.image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.image.setImageResource(R.drawable.avatar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allFriendsTillNow.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
       ImageView image;
       TextView userName,lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.profilePic);
            userName =  itemView.findViewById(R.id.userName);
            lastMessage=itemView.findViewById(R.id.lastMessage);
        }

    }
}
