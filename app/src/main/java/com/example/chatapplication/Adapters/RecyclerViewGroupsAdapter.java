package com.example.chatapplication.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
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
import com.example.chatapplication.ChatDetailActivity;
import com.example.chatapplication.GroupChatDetailActivity;
import com.example.chatapplication.Models.Groups;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerViewGroupsAdapter extends RecyclerView.Adapter<RecyclerViewGroupsAdapter.ViewHolder>
{
    ArrayList<Groups> groups;
    Activity context;
    Users myProfile;
    FirebaseFirestore database;

    public RecyclerViewGroupsAdapter(ArrayList<Groups> groups,Users myProfile, Activity context) {
        this.groups = groups;
        this.context = context;
        this.myProfile = myProfile;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.sample_group,parent,false);
        return new RecyclerViewGroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groups group= groups.get(position);
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(group.getDpMidLocation());
        try {
            setProfilePic(storageReference,holder);
        } catch (Exception e) {
            holder.image.setImageResource(R.drawable.avatar);
        }
        holder.groupName.setText((String)group.getGroupName());
        holder.lastMessageOfGroup.setText("lastmessage");
        database= FirebaseFirestore.getInstance();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatDetailActivity.class);
                intent.putExtra("group",(Serializable) group);
                intent.putExtra("myProfile",(Serializable) myProfile);
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
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView groupName,lastMessageOfGroup;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.profilePicOfGroup);
            groupName =  itemView.findViewById(R.id.groupName);
            lastMessageOfGroup=itemView.findViewById(R.id.lastMessageOfGroup);
        }
    }
}
